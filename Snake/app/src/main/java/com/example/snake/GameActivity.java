package com.example.snake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private SnakeGame snakeGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SurfaceView gameView = findViewById(R.id.game_view);
        String snakeColorString = getIntent().getStringExtra("snake_color");
        int snakeColor = Color.parseColor(snakeColorString);
        String difficulty = getIntent().getStringExtra("difficulty");

        snakeGame = new SnakeGame(gameView, snakeColor, difficulty);
        snakeGame.start();

        Button upButton = findViewById(R.id.up_button);
        Button downButton = findViewById(R.id.down_button);
        Button leftButton = findViewById(R.id.left_button);
        Button rightButton = findViewById(R.id.right_button);

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snakeGame.setDirection(SnakeGame.Direction.UP);
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snakeGame.setDirection(SnakeGame.Direction.DOWN);
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snakeGame.setDirection(SnakeGame.Direction.LEFT);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snakeGame.setDirection(SnakeGame.Direction.RIGHT);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (snakeGame != null) {
            snakeGame.stopGame();
        }
    }

    public void showGameOverDialog(final int snakeLength) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(GameActivity.this)
                        .setTitle("Game Over")
                        .setMessage("You lost the game!\nLength of your snake: " + snakeLength)
                        .setPositiveButton("Return to Main Menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            }
        });
    }
}