package com.example.pokeappv2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Random;

public class PokemonTargetActivity extends AppCompatActivity {

    private TextView tvTimer, tvScore, tvLives;
    private FrameLayout gameArea;
    private ImageView ivTarget;
    private Button btnStartGame;

    private int score = 0;
    private int lives = 3;
    private boolean isGameRunning = false;
    private boolean isBomb = false;

    private final Handler targetHandler = new Handler(Looper.getMainLooper());
    private Runnable targetRunnable;
    private CountDownTimer timer;
    private final Random random = new Random();

    // URLs de Sprites
    private final String TARGET_POKEMON_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"; // Pikachu
    private final String BOMB_POKEMON_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/100.png";   // Voltorb

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_target);

        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        tvLives = findViewById(R.id.tvLives);
        gameArea = findViewById(R.id.gameArea);
        ivTarget = findViewById(R.id.ivTarget);
        btnStartGame = findViewById(R.id.btnStartGame);

        btnStartGame.setOnClickListener(v -> startGame());

        ivTarget.setOnClickListener(v -> onTargetClicked());
    }

    private void startGame() {
        score = 0;
        lives = 3;
        isGameRunning = true;

        tvScore.setText("🎯 Puntos: " + score);
        tvLives.setText("❤️ Vidas: " + lives);
        btnStartGame.setEnabled(false);
        btnStartGame.setAlpha(0.5f);

        // Temporizador de 30 segundos
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("⏳ Tiempo: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                endGame("⏰ ¡TIEMPO AGOTADO!");
            }
        }.start();

        spawnTargetLoop();
    }

    private void spawnTargetLoop() {
        targetRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameRunning) return;

                moveTargetRandomly();

                // Cambia la posición cada 900 ms
                targetHandler.postDelayed(this, 900);
            }
        };
        targetHandler.post(targetRunnable);
    }

    private void moveTargetRandomly() {
        int areaWidth = gameArea.getWidth() - ivTarget.getWidth();
        int areaHeight = gameArea.getHeight() - ivTarget.getHeight();

        if (areaWidth <= 0 || areaHeight <= 0) return;

        // Posición aleatoria dentro del FrameLayout
        float randomX = random.nextInt(areaWidth);
        float randomY = random.nextInt(areaHeight);

        ivTarget.setX(randomX);
        ivTarget.setY(randomY);

        // 25% de probabilidad de que aparezca un Voltorb (Bomba)
        isBomb = random.nextInt(100) < 25;

        if (isBomb) {
            Glide.with(this).load(BOMB_POKEMON_URL).into(ivTarget);
        } else {
            Glide.with(this).load(TARGET_POKEMON_URL).into(ivTarget);
        }

        ivTarget.setVisibility(View.VISIBLE);
    }

    private void onTargetClicked() {
        if (!isGameRunning) return;

        ivTarget.setVisibility(View.INVISIBLE);

        if (isBomb) {
            lives--;
            tvLives.setText("❤️ Vidas: " + lives);
            Toast.makeText(this, "💥 ¡BOOM! Era Voltorb (-1 Vida)", Toast.LENGTH_SHORT).show();

            if (lives <= 0) {
                endGame("💀 ¡TE QUEDASTE SIN VIDAS!");
            }
        } else {
            score += 10;
            tvScore.setText("🎯 Puntos: " + score);
        }
    }

    private void endGame(String message) {
        isGameRunning = false;

        if (timer != null) timer.cancel();
        targetHandler.removeCallbacks(targetRunnable);

        ivTarget.setVisibility(View.INVISIBLE);
        btnStartGame.setEnabled(true);
        btnStartGame.setAlpha(1.0f);
        tvTimer.setText("⏳ Tiempo: 0s");

        Toast.makeText(this, message + "\nPuntuación final: " + score, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
        targetHandler.removeCallbacks(targetRunnable);
    }
}