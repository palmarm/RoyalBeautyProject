package com.example.royalbeautyproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<Service> selectedServices = new ArrayList<>();
    private double totalAmount = 0.0;
    private ConstraintLayout layout;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        layout = findViewById(R.id.activity_home); // Use the correct ID for the root layout

        applyUserPreferences();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AccessibilityOptionsActivity.class);
            startActivity(intent);
        });

        // Retrieve passed services and total amount from intent
        if (getIntent().hasExtra("selected_services")) {
            selectedServices = getIntent().getParcelableArrayListExtra("selected_services");
        }
        if (getIntent().hasExtra("total_amount")) {
            totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("shared_preferences", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        Toast.makeText(getApplicationContext(), "Welcome, " + username, Toast.LENGTH_SHORT).show();

        // Find views
        CardView cardNails = findViewById(R.id.cardNails);
        CardView cardHair = findViewById(R.id.cardHair);
        CardView cardMakeup = findViewById(R.id.cardMakeup);
        CardView cardViewAppointment = findViewById(R.id.cardViewAppointment);
        CardView cardCart = findViewById(R.id.cardCart);

        // Set click listeners
        cardNails.setOnClickListener(v -> navigateToNails());
        cardHair.setOnClickListener(v -> navigateToHair());
        cardMakeup.setOnClickListener(v -> navigateToMakeup());
        cardViewAppointment.setOnClickListener(v -> navigateToAppointment());
        cardCart.setOnClickListener(v -> navigateToCart());
    }

    private void applyUserPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply font size
        float fontSize = prefs.getFloat("fontSize", 16);
        TextView titleHome = findViewById(R.id.titleHome);
        titleHome.setTextSize(fontSize);

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

    private void navigateToNails() {
        Intent intent = new Intent(HomeActivity.this, NailsActivity.class);
        intent.putParcelableArrayListExtra("selected_services", selectedServices);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }

    private void navigateToHair() {
        Intent intent = new Intent(HomeActivity.this, HairActivity.class);
        intent.putParcelableArrayListExtra("selected_services", selectedServices);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }

    private void navigateToMakeup() {
        Intent intent = new Intent(HomeActivity.this, MakeupActivity.class);
        intent.putParcelableArrayListExtra("selected_services", selectedServices);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }

    private void navigateToAppointment() {
        Intent intent = new Intent(HomeActivity.this, AppointmentActivity.class);
        intent.putParcelableArrayListExtra("selected_services", selectedServices);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }

    private void navigateToCart() {
        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
        intent.putParcelableArrayListExtra("selected_services", selectedServices);
        intent.putExtra("total_amount", totalAmount);
        startActivity(intent);
    }
}
