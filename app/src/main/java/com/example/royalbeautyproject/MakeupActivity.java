package com.example.royalbeautyproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MakeupActivity extends AppCompatActivity implements ServiceAdapter.OnServiceSelectedListener {

    private RecyclerView recyclerViewServices;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList;
    private Button confirmButton;
    private Button saveButton;
    private Button addOtherServicesButton;
    private ArrayList<Service> selectedServices;
    private double totalCost = 0;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup);

        recyclerViewServices = findViewById(R.id.recyclerView);
        confirmButton = findViewById(R.id.confirmButton);
        saveButton = findViewById(R.id.saveButton);
        addOtherServicesButton = findViewById(R.id.addOtherServicesButton);
        ConstraintLayout layout = findViewById(R.id.activity_makeup); // Use the correct ID
        applyUserPreferences(layout);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
        serviceList = new ArrayList<>();

        // Initialize RecyclerView with services
        initializeServiceList();

        serviceAdapter = new ServiceAdapter(serviceList, this);
        recyclerViewServices.setAdapter(serviceAdapter);

        // Retrieve passed services and total amount from intent
        selectedServices = getIntent().getParcelableArrayListExtra("selected_services");
        if (selectedServices == null) {
            selectedServices = new ArrayList<>();
        } else {
            for (Service service : selectedServices) {
                totalCost += service.getPrice();
            }
        }
        totalCost += getIntent().getDoubleExtra("total_amount", 0.0);

        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(MakeupActivity.this, HomeActivity.class);
            intent.putParcelableArrayListExtra("selected_services", selectedServices);
            intent.putExtra("total_amount", totalCost);
            startActivity(intent);
        });

        confirmButton.setOnClickListener(v -> {
            if (!selectedServices.isEmpty()) {
                Intent intent = new Intent(MakeupActivity.this, AppointmentActivity.class);
                intent.putExtra("selected_services", selectedServices);
                intent.putExtra("total_cost", totalCost);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select at least one service.", Toast.LENGTH_SHORT).show();
            }
        });

        addOtherServicesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MakeupActivity.this, HomeActivity.class);
            intent.putParcelableArrayListExtra("selected_services", selectedServices);
            intent.putExtra("total_amount", totalCost);
            startActivity(intent);
        });
    }

    private void applyUserPreferences(ConstraintLayout layout) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply font size
        float fontSize = prefs.getFloat("fontSize", 16);
        TextView title = findViewById(R.id.titleHair); // Correct ID for HairActivity
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

    private void initializeServiceList() {
        // Adding makeup services with corresponding images
        serviceList.add(new Service("Scrubbing", 300, "makeup", R.drawable.scrubbing));
        serviceList.add(new Service("Half Facial (No Steaming)", 500, "makeup", R.drawable.half_facial));
        serviceList.add(new Service("Full Facial", 800, "makeup", R.drawable.full_facial));
        serviceList.add(new Service("Simple Makeup", 500, "makeup", R.drawable.simple_makeup));
        serviceList.add(new Service("Half Makeup (No Lashes)", 800, "makeup", R.drawable.half_makeup));
        serviceList.add(new Service("Full Makeup", 1000, "makeup", R.drawable.full_makeup));
    }

    @Override
    public void onServiceSelected(Service service, boolean isSelected) {
        if (isSelected) {
            selectedServices.add(service);
            totalCost += service.getPrice();
        } else {
            selectedServices.remove(service);
            totalCost -= service.getPrice();
        }
    }
}
