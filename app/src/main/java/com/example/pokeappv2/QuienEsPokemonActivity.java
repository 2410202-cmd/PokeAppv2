package com.example.pokeappv2;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuienEsPokemonActivity extends AppCompatActivity {

    private ImageView ivPokemonSilhouette;
    private TextView tvScore, tvStreak, tvPokemonNameResult;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnSkip;
    private Button[] optionButtons;

    private int currentScore = 0;
    private int currentStreak = 0;
    private String correctPokemonName = "";
    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quien_es_pokemon);

        ivPokemonSilhouette = findViewById(R.id.ivPokemonSilhouette);
        tvScore = findViewById(R.id.tvScore);
        tvStreak = findViewById(R.id.tvStreak);
        tvPokemonNameResult = findViewById(R.id.tvPokemonNameResult);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);
        btnSkip = findViewById(R.id.btnSkip);

        optionButtons = new Button[]{btnOption1, btnOption2, btnOption3, btnOption4};

        for (Button btn : optionButtons) {
            btn.setOnClickListener(v -> checkAnswer(btn.getText().toString()));
        }

        btnSkip.setOnClickListener(v -> loadNextRound());

        loadNextRound();
    }

    private void loadNextRound() {
        setButtonsEnabled(false);
        tvPokemonNameResult.setText("¿ ? ?");
        ivPokemonSilhouette.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        int correctId = random.nextInt(151) + 1; // Kanto (Anime Clásico)

        executor.execute(() -> {
            try {
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + correctId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == 200) {
                    InputStream stream = conn.getInputStream();
                    Scanner scanner = new Scanner(stream).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";

                    JSONObject json = new JSONObject(response);
                    correctPokemonName = json.getString("name").toUpperCase();
                    String spriteUrl = json.getJSONObject("sprites")
                            .getJSONObject("other")
                            .getJSONObject("official-artwork")
                            .getString("front_default");

                    List<String> options = new ArrayList<>();
                    options.add(correctPokemonName);

                    while (options.size() < 4) {
                        int wrongId = random.nextInt(151) + 1;
                        if (wrongId != correctId) {
                            String wrongName = fetchPokemonNameSync(wrongId);
                            if (!wrongName.isEmpty() && !options.contains(wrongName)) {
                                options.add(wrongName);
                            }
                        }
                    }

                    Collections.shuffle(options);

                    handler.post(() -> {
                        Glide.with(this).load(spriteUrl).into(ivPokemonSilhouette);
                        for (int i = 0; i < 4; i++) {
                            optionButtons[i].setText(options.get(i));
                        }
                        setButtonsEnabled(true);
                    });
                }
            } catch (Exception e) {
                handler.post(() -> {
                    Toast.makeText(this, "Error al cargar el Pokémon", Toast.LENGTH_SHORT).show();
                    setButtonsEnabled(true);
                });
            }
        });
    }

    private String fetchPokemonNameSync(int id) {
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                InputStream stream = conn.getInputStream();
                Scanner scanner = new Scanner(stream).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                return new JSONObject(response).getString("name").toUpperCase();
            }
        } catch (Exception ignored) {}
        return "";
    }

    private void checkAnswer(String selectedName) {
        ivPokemonSilhouette.clearColorFilter(); // Revela el Pokémon a todo color
        tvPokemonNameResult.setText(correctPokemonName);
        setButtonsEnabled(false);

        if (selectedName.equals(correctPokemonName)) {
            currentScore += 10;
            currentStreak++;
            Toast.makeText(this, "¡Correcto! 🎉", Toast.LENGTH_SHORT).show();
        } else {
            currentStreak = 0;
            Toast.makeText(this, "¡Incorrecto! Era " + correctPokemonName, Toast.LENGTH_SHORT).show();
        }

        tvScore.setText("SCORE: " + currentScore);
        tvStreak.setText("Racha: " + currentStreak);

        handler.postDelayed(this::loadNextRound, 1800);
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button btn : optionButtons) {
            btn.setEnabled(enabled);
        }
        btnSkip.setEnabled(enabled);
    }
}