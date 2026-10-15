package com.example.pokeappv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BattleEmulatorActivity extends AppCompatActivity {

    private EditText etPlayerPokemon, etRivalPokemon;
    private Button btnLoadPlayer, btnRandomRival, btnStartAutoBattle;
    private TextView tvPlayerName, tvPlayerHpText, tvRivalName, tvRivalHpText, tvBattleLog;
    private ImageView ivPlayerGif, ivRivalGif;
    private ProgressBar pbPlayerHp, pbRivalHp;
    private ScrollView mainScrollView, scrollBattleLog;

    // Variables de estado de la batalla
    private Pokemon playerPokemon;
    private Pokemon rivalPokemon;
    private boolean isBattling = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_emulator);

        // 1. Vincular ScrollView y aplicar fondo
        mainScrollView = findViewById(R.id.mainScrollView);
        if (mainScrollView != null) {
            mainScrollView.setBackgroundResource(R.drawable.battle);
        }

        // 2. Vincular vistas
        initViews();

        // 3. Configurar Listeners
        btnLoadPlayer.setOnClickListener(v -> {
            String query = etPlayerPokemon.getText().toString().trim().toLowerCase();
            if (!query.isEmpty()) {
                fetchPokemonData(query, true);
            } else {
                Toast.makeText(this, "Ingresa nombre o ID del Pokémon", Toast.LENGTH_SHORT).show();
            }
        });

        btnRandomRival.setOnClickListener(v -> {
            int randomId = new Random().nextInt(898) + 1;
            etRivalPokemon.setText(String.valueOf(randomId));
            fetchPokemonData(String.valueOf(randomId), false);
        });

        btnStartAutoBattle.setOnClickListener(v -> {
            if (playerPokemon == null || rivalPokemon == null) {
                Toast.makeText(this, "Debes cargar ambos Pokémon primero con los botones Ball", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isBattling) return;

            startAutoBattle();
        });
    }

    private void initViews() {
        etPlayerPokemon = findViewById(R.id.etPlayerPokemon);
        etRivalPokemon = findViewById(R.id.etRivalPokemon);
        btnLoadPlayer = findViewById(R.id.btnLoadPlayer);
        btnRandomRival = findViewById(R.id.btnRandomRival);
        btnStartAutoBattle = findViewById(R.id.btnStartAutoBattle);

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvPlayerHpText = findViewById(R.id.tvPlayerHpText);
        tvRivalName = findViewById(R.id.tvRivalName);
        tvRivalHpText = findViewById(R.id.tvRivalHpText);
        tvBattleLog = findViewById(R.id.tvBattleLog);

        ivPlayerGif = findViewById(R.id.ivPlayerGif);
        ivRivalGif = findViewById(R.id.ivRivalGif);

        pbPlayerHp = findViewById(R.id.pbPlayerHp);
        pbRivalHp = findViewById(R.id.pbRivalHp);
        scrollBattleLog = findViewById(R.id.scrollBattleLog);
    }

    // Petición de API en segundo plano
    private void fetchPokemonData(String nameOrId, boolean isPlayer) {
        appendLog("Cargando " + (isPlayer ? "Jugador" : "Rival") + "...");

        executor.execute(() -> {
            try {
                URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + nameOrId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {
                    InputStream stream = conn.getInputStream();
                    Scanner scanner = new Scanner(stream).useDelimiter("\\A");
                    String response = scanner.hasNext() ? scanner.next() : "";

                    JSONObject json = new JSONObject(response);
                    String name = json.getString("name").toUpperCase();
                    int hp = json.getJSONArray("stats").getJSONObject(0).getInt("base_stat");
                    int attack = json.getJSONArray("stats").getJSONObject(1).getInt("base_stat");
                    int defense = json.getJSONArray("stats").getJSONObject(2).getInt("base_stat");
                    int speed = json.getJSONArray("stats").getJSONObject(5).getInt("base_stat");

                    // Carga robusta de GIFs animados (Showdown) o Sprites Oficiales
                    JSONObject sprites = json.getJSONObject("sprites");
                    JSONObject other = sprites.optJSONObject("other");
                    String gifUrl = "";

                    if (other != null && other.has("showdown")) {
                        gifUrl = other.getJSONObject("showdown").optString("front_default", "");
                    }
                    if (gifUrl.isEmpty() || gifUrl.equals("null")) {
                        gifUrl = sprites.optString("front_default", "");
                    }

                    Pokemon fetchedPokemon = new Pokemon(name, hp, attack, defense, speed, gifUrl);

                    handler.post(() -> {
                        if (isPlayer) {
                            playerPokemon = fetchedPokemon;
                            tvPlayerName.setText(playerPokemon.name);
                            pbPlayerHp.setMax(playerPokemon.maxHp);
                            pbPlayerHp.setProgress(playerPokemon.currentHp);
                            tvPlayerHpText.setText(playerPokemon.currentHp + " / " + playerPokemon.maxHp + " HP");
                            Glide.with(this).load(playerPokemon.gifUrl).into(ivPlayerGif);
                            appendLog("¡" + playerPokemon.name + " cargado exitosamente!");
                        } else {
                            rivalPokemon = fetchedPokemon;
                            tvRivalName.setText(rivalPokemon.name);
                            pbRivalHp.setMax(rivalPokemon.maxHp);
                            pbRivalHp.setProgress(rivalPokemon.currentHp);
                            tvRivalHpText.setText(rivalPokemon.currentHp + " / " + rivalPokemon.maxHp + " HP");
                            Glide.with(this).load(rivalPokemon.gifUrl).into(ivRivalGif);
                            appendLog("¡" + rivalPokemon.name + " cargado como rival!");
                        }
                    });
                } else {
                    handler.post(() -> appendLog("Error: Pokémon no encontrado."));
                }
            } catch (Exception e) {
                handler.post(() -> appendLog("Error de red: " + e.getLocalizedMessage()));
            }
        });
    }

    // Algoritmo de Batalla Automática
    private void startAutoBattle() {
        isBattling = true;
        btnStartAutoBattle.setEnabled(false);
        tvBattleLog.setText("--- ¡COMIENZA LA BATALLA! ---");

        // Restaurar HP antes de luchar
        playerPokemon.resetHp();
        rivalPokemon.resetHp();
        updateHpUi();

        executor.execute(() -> {
            int round = 1;

            while (playerPokemon.isAlive() && rivalPokemon.isAlive()) {
                final int currentRound = round;
                handler.post(() -> appendLog("\n--- TURNO " + currentRound + " ---"));

                // Determinar quién ataca primero según Velocidad
                boolean playerGoesFirst = playerPokemon.speed >= rivalPokemon.speed;

                if (playerGoesFirst) {
                    executeAttack(playerPokemon, rivalPokemon, false);
                    if (rivalPokemon.isAlive()) {
                        sleep(1200);
                        executeAttack(rivalPokemon, playerPokemon, true);
                    }
                } else {
                    executeAttack(rivalPokemon, playerPokemon, true);
                    if (playerPokemon.isAlive()) {
                        sleep(1200);
                        executeAttack(playerPokemon, rivalPokemon, false);
                    }
                }

                round++;
                sleep(1500);
            }

            // Resultado final
            handler.post(() -> {
                isBattling = false;
                btnStartAutoBattle.setEnabled(true);
                String winner;
                if (playerPokemon.isAlive()) {
                    winner = playerPokemon.name;
                    appendLog("\n🏆 ¡GANASTE LA BATALLA!");
                } else {
                    winner = rivalPokemon.name;
                    appendLog("\n💀 " + rivalPokemon.name + " TE HA DERROTADO.");
                }

                // Guardar batalla en el Historial con GIFs
                saveBattleToHistory(playerPokemon.name, rivalPokemon.name, winner);
            });
        });
    }

    private void executeAttack(Pokemon attacker, Pokemon defender, boolean isDefenderPlayer) {
        // Cálculo de daño corregido
        int baseDamage = Math.max(5, attacker.attack - (defender.defense / 2));
        int randomFactor = new Random().nextInt(10) + 1;
        int totalDamage = baseDamage + randomFactor;

        defender.currentHp = Math.max(0, defender.currentHp - totalDamage);

        handler.post(() -> {
            appendLog("💥 " + attacker.name + " ataca a " + defender.name + " causando " + totalDamage + " de daño.");
            updateHpUi();
        });
    }

    private void updateHpUi() {
        if (playerPokemon != null) {
            pbPlayerHp.setProgress(playerPokemon.currentHp);
            tvPlayerHpText.setText(playerPokemon.currentHp + " / " + playerPokemon.maxHp + " HP");
        }
        if (rivalPokemon != null) {
            pbRivalHp.setProgress(rivalPokemon.currentHp);
            tvRivalHpText.setText(rivalPokemon.currentHp + " / " + rivalPokemon.maxHp + " HP");
        }
    }

    private void appendLog(String text) {
        tvBattleLog.append("\n" + text);
        scrollBattleLog.post(() -> scrollBattleLog.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void saveBattleToHistory(String player, String rival, String winner) {
        SharedPreferences prefs = getSharedPreferences("PokeAppHistory", Context.MODE_PRIVATE);
        String currentHistory = prefs.getString("battle_list", "");

        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String playerGif = (playerPokemon != null) ? playerPokemon.gifUrl : "";
        String rivalGif = (rivalPokemon != null) ? rivalPokemon.gifUrl : "";

        // Formato estructurado para la pantalla de Historial
        String newEntry = timeStamp + ":::" + player + ":::" + rival + ":::" + winner + ":::" + playerGif + ":::" + rivalGif + "||";

        prefs.edit().putString("battle_list", newEntry + currentHistory).apply();
        Toast.makeText(this, "¡Batalla guardada en el Historial!", Toast.LENGTH_SHORT).show();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {}
    }

    // Clase Modelo Pokémon interna
    private static class Pokemon {
        String name, gifUrl;
        int maxHp, currentHp, attack, defense, speed;

        Pokemon(String name, int hp, int attack, int defense, int speed, String gifUrl) {
            this.name = name;
            this.maxHp = hp;
            this.currentHp = hp;
            this.attack = attack;
            this.defense = defense;
            this.speed = speed;
            this.gifUrl = gifUrl;
        }

        void resetHp() {
            this.currentHp = this.maxHp;
        }

        boolean isAlive() {
            return currentHp > 0;
        }
    }
}