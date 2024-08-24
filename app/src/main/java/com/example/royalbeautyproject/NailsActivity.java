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

public class NailsActivity extends AppCompatActivity implements ServiceAdapter.OnServiceSelectedListener {

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
        setContentView(R.layout.activity_nails);

        recyclerViewServices = findViewById(R.id.recyclerView);
        confirmButton = findViewById(R.id.confirmButton);
        addOtherServicesButton = findViewById(R.id.addOtherServicesButton);
        saveButton = findViewById(R.id.saveButton);
        ConstraintLayout layout = findViewById(R.id.activity_nails); // Use the correct ID
        applyUserPreferences(layout);

        recyclerViewServices.setLayoutManager(new LinearLayoutManager(this));
        serviceList = new ArrayList<>();

        // Retrieve passed services and total amount from intent
        selectedServices = getIntent().getParcelableArrayListExtra("selected_services");
        if (selectedServices == null) {
            selectedServices = new ArrayList<>();
        }
        totalCost = getIntent().getDoubleExtra("total_amount", 0.0);

        // Initialize RecyclerView with services
        initializeServiceList();

        serviceAdapter = new ServiceAdapter(serviceList, this);
        recyclerViewServices.setAdapter(serviceAdapter);

        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(NailsActivity.this, HomeActivity.class);
            intent.putParcelableArrayListExtra("selected_services", new ArrayList<>(serviceAdapter.getSelectedServices()));
            intent.putExtra("total_amount", totalCost);
            startActivity(intent);
        });
        confirmButton.setOnClickListener(v -> {
            if (totalCost > 0) {
                Intent intent = new Intent(NailsActivity.this, AppointmentActivity.class);
                intent.putExtra("selected_services", new ArrayList<>(serviceAdapter.getSelectedServices()));
                intent.putExtra("total_cost", totalCost);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select at least one service.", Toast.LENGTH_SHORT).show();
            }
        });

        addOtherServicesButton.setOnClickListener(v -> {
            Intent intent = new Intent(NailsActivity.this, HomeActivity.class);
            intent.putParcelableArrayListExtra("selected_services", selectedServices);
            intent.putExtra("total_amount", totalCost);
            startActivity(intent);
        });
    }

    private void applyUserPreferences(ConstraintLayout layout) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Apply font size
        float fontSize = prefs.getFloat("fontSize", 16);
        TextView title = findViewById(R.id.titleNails); // Use the correct ID
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
        // Adding nail services with corresponding images
        serviceList.add(new Service("Manicure", 300, "nails", R.drawable.manicure));
        serviceList.add(new Service("Pedicure", 400, "nails", R.drawable.pedicure));
        serviceList.add(new Service("Tips + Plain Gel", 600, "nails", R.drawable.tips_plain_gel));
        serviceList.add(new Service("Tips + Art", 700, "nails", R.drawable.tips_art));
        serviceList.add(new Service("Tips + Complex Art", 800, "nails", R.drawable.tips_complex_art));
        serviceList.add(new Service("Acrylics", 1500, "nails", R.drawable.acrylics));
    }

    @Override
    public void onServiceSelected(Service service, boolean isSelected) {
        if (isSelected) {
            totalCost += service.getPrice();
        } else {
            totalCost -= service.getPrice();
        }
    }
}
