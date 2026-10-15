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

public class TorrePokemonActivity extends AppCompatActivity {

    private ImageView ivPlayerSprite, ivEnemySprite;
    private ProgressBar pbPlayerHp, pbEnemyHp;
    private TextView tvPlayerName, tvBossTitle, tvPlayerHpText, tvEnemyHpText, tvBattleLog, tvFloorText;
    private Button btnAttack1, btnAttack2, btnAttack3, btnAttack4, btnSelectPokemon;

    private int playerMaxHp = 100, playerHp = 100;
    private int enemyMaxHp = 100, enemyHp = 100;
    private int currentFloor = 0;
    private boolean isPlayerTurn = true;

    // JEFES DE LA TORRE (PROFESORES)
    // Formato: { "Nombre Profesor", "ID Pokémon", "Nombre Pokémon", "HP Base" }
    private final String[][] professorBosses = {
            {"PROFE VÍCTOR", "25", "Pikachu", "90"},
            {"PROFE CAMPA", "6", "Charizard", "110"},
            {"PROFE BADI", "9", "Blastoise", "130"},
            {"PROFE EDGAR", "150", "Mewtwo", "150"},
            {"PROFE OAKTAVIO", "149", "Dragonite", "180"} // Boss Supremo Final
    };

    private final String[][] playerPokemonList = {
            {"25", "Pikachu"}, {"6", "Charizard"}, {"9", "Blastoise"},
            {"3", "Venusaur"}, {"94", "Gengar"}, {"150", "Mewtwo"}
    };

    private String selectedPlayerName = "Pikachu";
    private String selectedPlayerId = "25";

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torre_pokemon);

        ivPlayerSprite = findViewById(R.id.ivPlayerSprite);
        ivEnemySprite = findViewById(R.id.ivEnemySprite);
        pbPlayerHp = findViewById(R.id.pbPlayerHp);
        pbEnemyHp = findViewById(R.id.pbEnemyHp);
        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvBossTitle = findViewById(R.id.tvBossTitle);
        tvPlayerHpText = findViewById(R.id.tvPlayerHpText);
        tvEnemyHpText = findViewById(R.id.tvEnemyHpText);
        tvBattleLog = findViewById(R.id.tvBattleLog);
        tvFloorText = findViewById(R.id.tvFloorText);

        btnAttack1 = findViewById(R.id.btnAttack1);
        btnAttack2 = findViewById(R.id.btnAttack2);
        btnAttack3 = findViewById(R.id.btnAttack3);
        btnAttack4 = findViewById(R.id.btnAttack4);
        btnSelectPokemon = findViewById(R.id.btnSelectPokemon);

        startTowerChallenge();

        btnAttack1.setOnClickListener(v -> executePlayerTurn("Ataque Directo", 20, false));
        btnAttack2.setOnClickListener(v -> executePlayerTurn("Golpe Critico", 32, false));
        btnAttack3.setOnClickListener(v -> executePlayerTurn("Recuperar HP", -25, true));
        btnAttack4.setOnClickListener(v -> executePlayerTurn("Ataque Final", 45, false));

        btnSelectPokemon.setOnClickListener(v -> showPokemonPicker());
    }

    private void startTowerChallenge() {
        currentFloor = 0;
        playerHp = playerMaxHp;
        loadFloorBoss();
    }

    private void loadFloorBoss() {
        if (currentFloor >= professorBosses.length) {
            tvBattleLog.setText("¡FELICIDADES! ¡Derrotaste a todos los Profesores!");
            showVictoryDialog();
            return;
        }

        String profName = professorBosses[currentFloor][0];
        String enemyPokeId = professorBosses[currentFloor][1];
        String enemyPokeName = professorBosses[currentFloor][2];
        enemyMaxHp = Integer.parseInt(professorBosses[currentFloor][3]);
        enemyHp = enemyMaxHp;

        tvFloorText.setText("PISO: " + (currentFloor + 1));
        tvBossTitle.setText(profName + " (" + enemyPokeName + ")");
        tvPlayerName.setText(selectedPlayerName.toUpperCase());

        updateHpBars();

        String playerUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + selectedPlayerId + ".png";
        String enemyUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + enemyPokeId + ".png";

        Glide.with(this).load(playerUrl).into(ivPlayerSprite);
        Glide.with(this).load(enemyUrl).into(ivEnemySprite);

        tvBattleLog.setText("Piso " + (currentFloor + 1) + ": ¡" + profName + " saca a " + enemyPokeName + "!");
        isPlayerTurn = true;
        setButtonsEnabled(true);
    }

    private void showPokemonPicker() {
        String[] names = new String[playerPokemonList.length];
        for (int i = 0; i < playerPokemonList.length; i++) {
            names[i] = playerPokemonList[i][1];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige tu Pokémon para la Torre");
        builder.setItems(names, (dialog, which) -> {
            selectedPlayerId = playerPokemonList[which][0];
            selectedPlayerName = playerPokemonList[which][1];
            startTowerChallenge();
        });
        builder.show();
    }

    private void executePlayerTurn(String attackName, int value, boolean isHeal) {
        if (!isPlayerTurn) return;

        setButtonsEnabled(false);
        animateAttack(ivPlayerSprite);

        if (isHeal) {
            playerHp = Math.min(playerMaxHp, playerHp + Math.abs(value));
            tvBattleLog.setText(selectedPlayerName + " recupero energia (+25 HP)");
        } else {
            enemyHp = Math.max(0, enemyHp - value);
            animateDamage(ivEnemySprite);
            tvBattleLog.setText(selectedPlayerName + " uso " + attackName + " e hizo " + value + " daño!");
        }

        updateHpBars();

        if (enemyHp <= 0) {
            currentFloor++;
            tvBattleLog.setText("¡Derrotaste al " + professorBosses[currentFloor - 1][0] + "!");
            new Handler(Looper.getMainLooper()).postDelayed(this::loadFloorBoss, 1500);
            return;
        }

        isPlayerTurn = false;
        new Handler(Looper.getMainLooper()).postDelayed(this::executeAiTurn, 1400);
    }

    private void executeAiTurn() {
        String profName = professorBosses[currentFloor][0];
        int damage = 12 + random.nextInt(15) + (currentFloor * 4);

        animateAttack(ivEnemySprite);
        playerHp = Math.max(0, playerHp - damage);
        animateDamage(ivPlayerSprite);

        updateHpBars();

        tvBattleLog.setText("¡" + profName + " contraataco causando " + damage + " de daño!");

        if (playerHp <= 0) {
            tvBattleLog.setText("¡Has sido derrotado en el Piso " + (currentFloor + 1) + "!");
            showGameOverDialog();
        } else {
            isPlayerTurn = true;
            setButtonsEnabled(true);
        }
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡Derrotado en la Torre!");
        builder.setMessage("Llegaste hasta el Piso " + (currentFloor + 1) + " contra " + professorBosses[currentFloor][0] + ".");
        builder.setPositiveButton("Reintentar Torre", (dialog, which) -> startTowerChallenge());
        builder.setCancelable(false);
        builder.show();
    }

    private void showVictoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¡CAMPEON DE LA TORRE!");
        builder.setMessage("¡Derrotaste a todos los Profesores! Te consagraste como el Maestro Definitivo.");
        builder.setPositiveButton("Jugar de nuevo", (dialog, which) -> startTowerChallenge());
        builder.setCancelable(false);
        builder.show();
    }

    private void updateHpBars() {
        pbPlayerHp.setMax(playerMaxHp);
        pbPlayerHp.setProgress(playerHp);

        pbEnemyHp.setMax(enemyMaxHp);
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