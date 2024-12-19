package com.example.snake;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner colorSpinner;
    private Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colorSpinner = findViewById(R.id.color_spinner);
        difficultySpinner = findViewById(R.id.difficulty_spinner);
        Button startButton = findViewById(R.id.start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedColor = colorSpinner.getSelectedItem().toString();
                String selectedDifficulty = difficultySpinner.getSelectedItem().toString();
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("snake_color", selectedColor);
                intent.putExtra("difficulty", selectedDifficulty);
                startActivity(intent);
            }
        });
    }
}