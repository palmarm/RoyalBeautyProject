package com.example.royalbeautyproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private TextView serviceDetailsTextView;
    private TextView totalPriceTextView;
    private TextView appointmentDetailsTextView;
    private Button updateButton;
    private Button checkoutButton;
    private Button cancelButton;
    private TextToSpeech tts;

    private List<Service> selectedServices;
    private double totalAmount;
    private long appointmentTime;
    private String appointmentId; // Store the appointment ID

    private DatabaseReference databaseAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Retrieve the appointment ID
        appointmentId = getIntent().getStringExtra("appointment_id");

        serviceDetailsTextView = findViewById(R.id.serviceDetails);
        totalPriceTextView = findViewById(R.id.totalPrice);
        appointmentDetailsTextView = findViewById(R.id.appointmentDetails);
        updateButton = findViewById(R.id.updateButton);
        checkoutButton = findViewById(R.id.checkoutButton);
        cancelButton = findViewById(R.id.cancelButton);
        ConstraintLayout layout = findViewById(R.id.activity_cart); // Use the correct ID
        applyUserPreferences(layout);

        // Initialize Firebase Database
        databaseAppointments = FirebaseDatabase.getInstance().getReference("appointments");

        // Get the selected services, total amount, appointment time, and appointment ID from the intent
        Intent intent = getIntent();
        selectedServices = intent.getParcelableArrayListExtra("selected_services");
        totalAmount = intent.getDoubleExtra("total_amount", 0.0);
        appointmentTime = intent.getLongExtra("appointment_time", 0);
        appointmentId = intent.getStringExtra("appointment_id");

        // Log the appointment ID to check if it is retrieved correctly
        Log.d("CartActivity", "Appointment ID: " + appointmentId);

        if (selectedServices == null) {
            selectedServices = new ArrayList<>();
        }

        // Display the selected service details
        StringBuilder serviceDetails = new StringBuilder();
        for (Service service : selectedServices) {
            serviceDetails.append(service.getName()).append(" - Ksh ").append(service.getPrice()).append("\n");
        }
        serviceDetailsTextView.setText(serviceDetails.toString());

        // Display the total amount
        totalPriceTextView.setText("Total: Ksh " + totalAmount);

        // Display the appointment details
        String appointmentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new java.util.Date(appointmentTime));
        appointmentDetailsTextView.setText("Appointment Date & Time: " + appointmentDateTime);

        // Set click listeners for buttons
        updateButton.setOnClickListener(v -> {
            if (appointmentId != null && !appointmentId.isEmpty()) {
                // Navigate to AppointmentActivity to update appointment details
                Intent updateIntent = new Intent(CartActivity.this, AppointmentActivity.class);
                updateIntent.putParcelableArrayListExtra("selected_services", new ArrayList<>(selectedServices));
                updateIntent.putExtra("total_amount", totalAmount);
                updateIntent.putExtra("appointment_time", appointmentTime);
                updateIntent.putExtra("appointment_id", appointmentId); // Pass the appointment ID to update
                startActivity(updateIntent);
            } else {
                // Navigate to HomeActivity to add other services
                Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
            finish(); // Close CartActivity
        });

        checkoutButton.setOnClickListener(v -> {
            AppointmentActivity.Appointment appointment;
            if (appointmentId != null && !appointmentId.isEmpty()) {
                // Update existing appointment in Firebase if needed
                appointment = new AppointmentActivity.Appointment(selectedServices, totalAmount, appointmentTime, appointmentId);
                databaseAppointments.child(appointmentId).setValue(appointment)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CartActivity.this, "Appointment updated and checked out", Toast.LENGTH_LONG).show();
                                // Navigate to HomeActivity or another appropriate activity
                                Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                finish(); // Close CartActivity
                            } else {
                                Toast.makeText(CartActivity.this, "Failed to update appointment", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                // Save a new appointment to Firebase
                appointment = new AppointmentActivity.Appointment(selectedServices, totalAmount, appointmentTime, null);
                databaseAppointments.push().setValue(appointment)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CartActivity.this, "Appointment booked and checked out", Toast.LENGTH_LONG).show();
                                // Navigate to HomeActivity or another appropriate activity
                                Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                finish(); // Close CartActivity
                            } else {
                                Toast.makeText(CartActivity.this, "Failed to book appointment", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        cancelButton.setOnClickListener(v -> {
            if (appointmentId != null && !appointmentId.isEmpty()) {
                // Reference to the Firebase database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId);

                // Remove the appointment from the database
                databaseReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(CartActivity.this, "Appointment cancelled successfully.", Toast.LENGTH_SHORT).show();
                        // Optionally, navigate back to another activity
                        Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to cancel appointment.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(CartActivity.this, "Appointment ID is missing.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyUserPreferences(ConstraintLayout layout) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply font size
        float fontSize = prefs.getFloat("fontSize", 16);
        TextView title = findViewById(R.id.titleCart); // Ensure this ID exists
        title.setTextSize(fontSize);

        // Apply background color
        int backgroundColor = prefs.getInt("backgroundColor", getResources().getColor(android.R.color.white));
        layout.setBackgroundColor(backgroundColor);

        // Apply voice navigation if needed
        boolean voiceNavEnabled = prefs.getBoolean("voiceNavEnabled", false);
        if (voiceNavEnabled) {
            if (tts == null) {
                tts = new TextToSpeech(this, status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        tts.setLanguage(Locale.US);
                        tts.speak("Voice navigation enabled", TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                });
            } else {
                tts.speak("Voice navigation enabled", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        // Release TextToSpeech resources
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
