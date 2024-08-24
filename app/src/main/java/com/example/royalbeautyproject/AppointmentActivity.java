package com.example.royalbeautyproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button bookAppointmentButton;
    private TextToSpeech tts;

    private List<Service> selectedServices;
    private double totalAmount;
    private String appointmentId; // Store the appointment ID

    private DatabaseReference databaseAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        ConstraintLayout layout = findViewById(R.id.activity_appointment); // Use the correct ID
        applyUserPreferences(layout);

        // Initialize Firebase Database
        databaseAppointments = FirebaseDatabase.getInstance().getReference("appointments");

        // Get the selected services and total amount from the intent
        Intent intent = getIntent();
        selectedServices = intent.getParcelableArrayListExtra("selected_services");
        totalAmount = intent.getDoubleExtra("total_amount", 0.0);

        if (selectedServices == null) {
            selectedServices = new ArrayList<>();
        }

        bookAppointmentButton.setOnClickListener(v -> {
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            Calendar appointmentTime = Calendar.getInstance();
            appointmentTime.set(year, month, day, hour, minute);

            // Set working hours for the selected date
            Calendar startWorkingHour = Calendar.getInstance();
            startWorkingHour.set(year, month, day, 7, 0, 0);

            Calendar endWorkingHour = Calendar.getInstance();
            endWorkingHour.set(year, month, day, 21, 0, 0);

            // Validate that the appointment time is within working hours (7:00 AM to 9:00 PM)
            if (appointmentTime.before(startWorkingHour) || appointmentTime.after(endWorkingHour)) {
                Toast.makeText(AppointmentActivity.this, "Please select a time between 7:00 AM and 9:00 PM", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate that the appointment time is in the future
            if (appointmentTime.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(AppointmentActivity.this, "Please select a future date and time", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate a unique ID for the appointment
            appointmentId = databaseAppointments.push().getKey();

            // Create an Appointment object
            Appointment appointment = new Appointment(selectedServices, totalAmount, appointmentTime.getTimeInMillis(), appointmentId);

            // Save the appointment to Firebase using the generated ID
            if (appointmentId != null) {
                databaseAppointments.child(appointmentId).setValue(appointment)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Navigate to CartActivity
                                Intent cartIntent = new Intent(AppointmentActivity.this, CartActivity.class);
                                cartIntent.putExtra("selected_services", new ArrayList<>(selectedServices));
                                cartIntent.putExtra("total_amount", totalAmount);
                                cartIntent.putExtra("appointment_time", appointmentTime.getTimeInMillis());
                                cartIntent.putExtra("appointment_id", appointmentId);
                                startActivity(cartIntent);
                            } else {
                                Toast.makeText(AppointmentActivity.this, "Failed to book appointment", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(AppointmentActivity.this, "Failed to generate appointment ID", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Define the Appointment class
    public static class Appointment {
        private List<Service> services;
        private double totalAmount;
        private long appointmentTime;
        private String appointmentId; // Field for appointment ID

        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
        public Appointment() {
        }

        public Appointment(List<Service> services, double totalAmount, long appointmentTime, String appointmentId) {
            this.services = services;
            this.totalAmount = totalAmount;
            this.appointmentTime = appointmentTime;
            this.appointmentId = appointmentId;
        }

        public List<Service> getServices() {
            return services;
        }

        public void setServices(List<Service> services) {
            this.services = services;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public long getAppointmentTime() {
            return appointmentTime;
        }

        public void setAppointmentTime(long appointmentTime) {
            this.appointmentTime = appointmentTime;
        }

        public String getAppointmentId() {
            return appointmentId;
        }

        public void setAppointmentId(String appointmentId) {
            this.appointmentId = appointmentId;
        }
    }


    private void applyUserPreferences(ConstraintLayout layout) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply font size
        float fontSize = prefs.getFloat("fontSize", 16);
        TextView title = findViewById(R.id.titleAppointment); // Use the correct ID
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
