package com.example.snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Handler;
import android.os.Looper;

public class SnakeGame extends Thread {

    private final SurfaceView surfaceView;
    private final Paint paint;
    private final int snakeColor;
    private final List<int[]> snake;
    private int[] food;
    private Direction direction;
    private boolean running;
    private boolean paused;
    private int sleepTime;
    private int gridSize;
    private int gridCountX;
    private int gridCountY;
    private static final String TAG = "SnakeGame";
    private final Handler handler;

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public SnakeGame(SurfaceView surfaceView, int snakeColor, String difficulty) {
        this.surfaceView = surfaceView;
        this.snakeColor = snakeColor;
        this.paint = new Paint();
        this.snake = new ArrayList<>();
        this.direction = Direction.RIGHT;
        this.running = true;
        this.paused = true;
        this.handler = new Handler(Looper.getMainLooper());

        snake.add(new int[]{5, 5});
        switch (difficulty) {
            case "Medium":
                this.sleepTime = 120;
                break;
            case "Hard":
                this.sleepTime = 100;
                break;
            case "Easy":
            default:
                this.sleepTime = 160;
                break;
        }
    }

    @Override
    public void run() {
        handler.post(gameLoop);
    }

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (running) {
                draw();
                if (!paused) {
                    update();
                    draw();
                }
                handler.postDelayed(this, sleepTime);
            }
        }
    };

    public void setDirection(Direction newDirection) {
        // Check if the new direction is opposite to the current direction
        if ((this.direction == Direction.UP && newDirection == Direction.DOWN) ||
                (this.direction == Direction.DOWN && newDirection == Direction.UP) ||
                (this.direction == Direction.LEFT && newDirection == Direction.RIGHT) ||
                (this.direction == Direction.RIGHT && newDirection == Direction.LEFT)) {
            // Reverse the snake
            reverseSnake();
        } else {
            this.direction = newDirection;
        }
        this.paused = false;
        //Log.d(TAG, "Direction set to: " + newDirection);
    }

    private void reverseSnake() {
        List<int[]> reversedSnake = new ArrayList<>();
        for (int i = snake.size() - 1; i >= 0; i--) {
            reversedSnake.add(snake.get(i));
        }
        snake.clear();
        snake.addAll(reversedSnake);
        // Reverse the direction
        switch (this.direction) {
            case UP:
                this.direction = Direction.DOWN;
                break;
            case DOWN:
                this.direction = Direction.UP;
                break;
            case LEFT:
                this.direction = Direction.RIGHT;
                break;
            case RIGHT:
                this.direction = Direction.LEFT;
                break;
        }
    }

    private void update() {
        int[] head = snake.get(0);
        int[] newHead = new int[]{head[0], head[1]};

        switch (direction) {
            case UP:
                newHead[1]--;
                break;
            case DOWN:
                newHead[1]++;
                break;
            case LEFT:
                newHead[0]--;
                break;
            case RIGHT:
                newHead[0]++;
                break;
        }

        if (newHead[0] < 0 || newHead[0] >= gridCountX || newHead[1] < 0 || newHead[1] >= gridCountY || isCollision(newHead)) {
            running = false;
            ((GameActivity) surfaceView.getContext()).showGameOverDialog(snake.size());
            return;
        }

        snake.add(0, newHead);

        if (newHead[0] == food[0] && newHead[1] == food[1]) {
            generateFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void draw() {
        SurfaceHolder holder = surfaceView.getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            // Calculate grid size and count based on SurfaceView dimensions
            gridSize = Math.min(surfaceView.getWidth() / 20, surfaceView.getHeight() / 20);
            gridCountX = surfaceView.getWidth() / gridSize;
            gridCountY = surfaceView.getHeight() / gridSize;

            //Log.d(TAG, "Grid Size: " + gridSize + ", Grid Count X: " + gridCountX + ", Grid Count Y: " + gridCountY);

            if (food == null) {
                generateFood();
            }

            canvas.drawColor(Color.BLACK);
            paint.setColor(snakeColor);
            for (int[] segment : snake) {
                //Log.d(TAG, "Drawing snake segment at: (" + segment[0] + ", " + segment[1] + ")");
                canvas.drawRect(segment[0] * gridSize, segment[1] * gridSize, (segment[0] + 1) * gridSize, (segment[1] + 1) * gridSize, paint);
            }
            paint.setColor(Color.WHITE);
            //Log.d(TAG, "Drawing food at: (" + food[0] + ", " + food[1] + ")");
            canvas.drawRect(food[0] * gridSize, food[1] * gridSize, (food[0] + 1) * gridSize, (food[1] + 1) * gridSize, paint);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void generateFood() {
        Random random = new Random();
        // Ensure gridCountX and gridCountY are positive
        if (gridCountX > 0 && gridCountY > 0) {
            food = new int[]{random.nextInt(gridCountX - 2) + 1, random.nextInt(gridCountY - 2) + 1};
        } else {
            food = new int[]{1, 1}; // Default value if gridCountX or gridCountY is not positive
        }
        //Log.d(TAG, "Generated food at: (" + food[0] + ", " + food[1] + ")");
    }

    private boolean isCollision(int[] position) {
        for (int[] segment : snake) {
            if (segment[0] == position[0] && segment[1] == position[1]) {
                return true;
            }
        }
        return false;
    }

    public void stopGame() {
        running = false;
        handler.removeCallbacks(gameLoop);
    }
}