package com.example.pokeappv2;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Random;

public class BattleVersusActivity extends AppCompatActivity {
    private ImageView ivPlayerSprite, ivEnemySprite;
    private ProgressBar pbPlayerHp, pbEnemyHp;
    private TextView tvPlayerName, tvEnemyName, tvPlayerHpText, tvEnemyHpText, tvBattleLog;
    private Button btnAttack1, btnAttack2, btnAttack3, btnAttack4, btnSelectPokemon;

    private int playerMaxHp = 100, playerHp = 100;
    private int enemyMaxHp = 100, enemyHp = 100;
    private boolean isPlayerTurn = true;

    // Lista de Pokémon disponibles para elegir
    private final String[][] pokemonList = {
            {"25", "Pikachu"}, {"6", "Charizard"}, {"9", "Blastoise"},
            {"3", "Venusaur"}, {"94", "Gengar"}, {"150", "Mewtwo"}
    };

    private String selectedPlayerName = "Pikachu";
    private String selectedPlayerId = "25";
    private String selectedEnemyName = "Charizard";
    private String selectedEnemyId = "6";

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_versus);

        ivPlayerSprite = findViewById(R.id.ivPlayerSprite);
        ivEnemySprite = findViewById(R.id.ivEnemySprite);
        pbPlayerHp = findViewById(R.id.pbPlayerHp);
        pbEnemyHp = findViewById(R.id.pbEnemyHp);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvEnemyName = findViewById(R.id.tvEnemyName);
        tvPlayerHpText = findViewById(R.id.tvPlayerHpText);
        tvEnemyHpText = findViewById(R.id.tvEnemyHpText);
        tvBattleLog = findViewById(R.id.tvBattleLog);

        btnAttack1 = findViewById(R.id.btnAttack1);
        btnAttack2 = findViewById(R.id.btnAttack2);
        btnAttack3 = findViewById(R.id.btnAttack3);
        btnAttack4 = findViewById(R.id.btnAttack4);
        btnSelectPokemon = findViewById(R.id.btnSelectPokemon);

        setupBattle();

        btnAttack1.setOnClickListener(v -> executePlayerTurn("Ataque Rapido", 15, false));
        btnAttack2.setOnClickListener(v -> executePlayerTurn("Impacto Fuerte", 25, false));
        btnAttack3.setOnClickListener(v -> executePlayerTurn("Curacion", -20, true)); // Cura HP
        btnAttack4.setOnClickListener(v -> executePlayerTurn("Ataque Especial", 35, false));

        btnSelectPokemon.setOnClickListener(v -> showPokemonPicker());
    }

    private void setupBattle() {
        // Asignar Pokémon del Rival aleatoriamente
        int enemyIdx = random.nextInt(pokemonList.length);
        selectedEnemyId = pokemonList[enemyIdx][0];
        selectedEnemyName = pokemonList[enemyIdx][1];

        tvPlayerName.setText(selectedPlayerName.toUpperCase());
        tvEnemyName.setText(selectedEnemyName.toUpperCase());

        playerHp = playerMaxHp;
        enemyHp = enemyMaxHp;

        updateHpBars();

        // Cargar Sprites desde PokeAPI
        String playerUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + selectedPlayerId + ".png";
        String enemyUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + selectedEnemyId + ".png";

        Glide.with(this).load(playerUrl).into(ivPlayerSprite);
        Glide.with(this).load(enemyUrl).into(ivEnemySprite);

        tvBattleLog.setText("¡Un " + selectedEnemyName + " salvaje aparecio!");
        setButtonsEnabled(true);
        isPlayerTurn = true;
    }

    private void showPokemonPicker() {
        String[] names = new String[pokemonList.length];
        for (int i = 0; i < pokemonList.length; i++) {
            names[i] = pokemonList[i][1];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu Pokémon");
        builder.setItems(names, (dialog, which) -> {
            selectedPlayerId = pokemonList[which][0];
            selectedPlayerName = pokemonList[which][1];
            setupBattle();
        });
        builder.show();
    }

    private void executePlayerTurn(String attackName, int value, boolean isHeal) {
        if (!isPlayerTurn) return;

        setButtonsEnabled(false);
        animateAttack(ivPlayerSprite);

        if (isHeal) {
            playerHp = Math.min(playerMaxHp, playerHp + Math.abs(value));
            tvBattleLog.setText(selectedPlayerName + " uso " + attackName + " y se recupero!");
        } else {
            enemyHp = Math.max(0, enemyHp - value);
            animateDamage(ivEnemySprite);
            tvBattleLog.setText(selectedPlayerName + " uso " + attackName + " e hizo " + value + " de daño!");
        }

        updateHpBars();

        if (enemyHp <= 0) {
            tvBattleLog.setText("¡Ganaste! " + selectedEnemyName + " ha sido derrotado.");
            return;
        }

        // Turno de la IA tras 1.5 segundos
        isPlayerTurn = false;
        new Handler(Looper.getMainLooper()).postDelayed(this::executeAiTurn, 1500);
    }

    private void executeAiTurn() {
        int damage = 10 + random.nextInt(20);

        animateAttack(ivEnemySprite);
        playerHp = Math.max(0, playerHp - damage);
        animateDamage(ivPlayerSprite);

        updateHpBars();

        tvBattleLog.setText(selectedEnemyName + " contraataco e hizo " + damage + " de daño!");

        if (playerHp <= 0) {
            tvBattleLog.setText("¡Has sido derrotado por " + selectedEnemyName + "!");
        } else {
            isPlayerTurn = true;
            setButtonsEnabled(true);
        }
    }

    private void updateHpBars() {
        pbPlayerHp.setProgress(playerHp);
        pbEnemyHp.setProgress(enemyHp);

        tvPlayerHpText.setText(playerHp + "/" + playerMaxHp + " HP");
        tvEnemyHpText.setText(enemyHp + "/" + enemyMaxHp + " HP");
    }

    private void animateAttack(ImageView view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f, 0f);
        anim.setDuration(250);
        anim.start();
    }

    private void animateDamage(ImageView view) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.2f, 1f);
        anim.setDuration(200);
        anim.start();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnAttack1.setEnabled(enabled);
        btnAttack2.setEnabled(enabled);
        btnAttack3.setEnabled(enabled);
        btnAttack4.setEnabled(enabled);
    }
}