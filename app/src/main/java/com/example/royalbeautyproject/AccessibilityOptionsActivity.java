package com.example.royalbeautyproject;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.Locale;

public class AccessibilityOptionsActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_options);

        layout = findViewById(R.id.accessibilityLayout);

        setupFontSizeControl();
        setupBackgroundColorControl();
        setupVoiceNavigationControl();
    }

    private void setupFontSizeControl() {
        SeekBar fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Save font size preference
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putFloat("fontSize", progress);
                editor.apply();

                // Apply font size
                applyFontSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Load saved font size preference
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        float fontSize = prefs.getFloat("fontSize", 16);
        fontSizeSeekBar.setProgress((int) fontSize);
        applyFontSize(fontSize);
    }

    private void applyFontSize(float fontSize) {
        // Example: Apply font size to TextView in this activity
        TextView sampleText = findViewById(R.id.sampleText);
        sampleText.setTextSize(fontSize);
    }

    private void setupBackgroundColorControl() {
        RadioGroup colorRadioGroup = findViewById(R.id.colorRadioGroup);
        colorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int color = getColorFromRadioButtonId(checkedId);
            // Save background color preference
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("backgroundColor", color);
            editor.apply();

            // Apply background color
            applyBackgroundColor(color);
        });

        // Load saved background color preference
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int color = prefs.getInt("backgroundColor", getResources().getColor(android.R.color.white));
        applyBackgroundColor(color);
    }

    private int getColorFromRadioButtonId(int radioButtonId) {
        if (radioButtonId == R.id.radioColorBlue) {
            return Color.parseColor("#33B5E5"); // Custom Blue
        } else if (radioButtonId == R.id.radioColorGreen) {
            return Color.parseColor("#99CC00"); // Custom Green
        } else if (radioButtonId == R.id.radioColorRed) {
            return Color.parseColor("#FF4444"); // Custom Red
        } else if (radioButtonId == R.id.radioColorWhite) {
            return Color.parseColor("#FFFFFF"); // Custom White
        } else if (radioButtonId == R.id.radioColorGray) {
            return Color.parseColor("#808080"); // Custom Gray
        }
        else {
            return Color.WHITE; // Default color
        }
    }

    private void applyBackgroundColor(int color) {
        layout.setBackgroundColor(color);
    }

    private void setupVoiceNavigationControl() {
        Button enableVoiceNavButton = findViewById(R.id.enableVoiceNavButton);
        enableVoiceNavButton.setOnClickListener(view -> {
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
        });
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
