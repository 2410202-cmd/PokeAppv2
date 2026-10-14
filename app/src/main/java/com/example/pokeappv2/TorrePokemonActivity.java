package com.example.pokeappv2;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TorrePokemonActivity extends AppCompatActivity {

    private ImageView ivPlayerSprite, ivEnemySprite;
    private ProgressBar pbPlayerHp, pbEnemyHp;
    private TextView tvPlayerName, tvBossTitle, tvPlayerHpText, tvEnemyHpText, tvBattleLog, tvFloorText;
    private TextView tvDamageFloatEnemy, tvDamageFloatPlayer;
    private Button btnAttack1, btnAttack2, btnAttack3, btnAttack4, btnSelectPokemon;

    private int playerMaxHp = 120, playerHp = 120;
    private int enemyMaxHp = 100, enemyHp = 100;

    private int floorIndex = 0;
    private int currentEnemyPokemonIndex = 0;
    private int currentPlayerPokemonIndex = 0;
    private boolean isPlayerTurn = true;

    private static class PokemonInfo {
        String id;
        String nombre;
        int hp;

        PokemonInfo(String id, String nombre, int hp) {
            this.id = id;
            this.nombre = nombre;
            this.hp = hp;
        }
    }

    private static class MaestroData {
        String nombre;
        PokemonInfo[] team;

        MaestroData(String nombre, PokemonInfo[] team) {
            this.nombre = nombre;
            this.team = team;
        }
    }

    private static class PokemonMoveSet {
        String m1Name; int m1Val; boolean m1IsHeal;
        String m2Name; int m2Val; boolean m2IsHeal;
        String m3Name; int m3Val; boolean m3IsHeal;
        String m4Name; int m4Val; boolean m4IsHeal;

        PokemonMoveSet(String m1, int v1, boolean h1,
                       String m2, int v2, boolean h2,
                       String m3, int v3, boolean h3,
                       String m4, int v4, boolean h4) {
            this.m1Name = m1; this.m1Val = v1; this.m1IsHeal = h1;
            this.m2Name = m2; this.m2Val = v2; this.m2IsHeal = h2;
            this.m3Name = m3; this.m3Val = v3; this.m3IsHeal = h3;
            this.m4Name = m4; this.m4Val = v4; this.m4IsHeal = h4;
        }
    }

    // LISTA COMPLETA DE POKÉMON PARA SORTEO DE EQUIPO JUGADOR
    private final String[][] allAvailablePokemon = {
            {"25", "Pikachu"}, {"6", "Charizard"}, {"9", "Blastoise"},
            {"3", "Venusaur"}, {"94", "Gengar"}, {"150", "Mewtwo"},
            {"448", "Lucario"}, {"149", "Dragonite"}, {"130", "Gyarados"},
            {"143", "Snorlax"}, {"257", "Blaziken"}, {"254", "Sceptile"},
            {"260", "Swampert"}, {"445", "Garchomp"}, {"131", "Lapras"}
    };

    private final List<PokemonInfo> playerTeam = new ArrayList<>();

    private final MaestroData[] maestrosList = {
            // Piso 1: Maestro Víctor
            new MaestroData("Maestro Víctor", new PokemonInfo[]{
                    new PokemonInfo("68", "Machamp", 100),
                    new PokemonInfo("257", "Blaziken", 115),
                    new PokemonInfo("448", "Lucario", 130)
            }),
            // Piso 2: Maestro Everardo
            new MaestroData("Maestro Everardo", new PokemonInfo[]{
                    new PokemonInfo("429", "Mismagius", 110),
                    new PokemonInfo("609", "Chandelure", 125),
                    new PokemonInfo("94", "Gengar", 140)
            }),
            // Piso 3: Maestro Edgar
            new MaestroData("Maestro Edgar", new PokemonInfo[]{
                    new PokemonInfo("149", "Dragonite", 120),
                    new PokemonInfo("373", "Salamence", 135),
                    new PokemonInfo("445", "Garchomp", 150)
            }),
            // Piso 4: Maestro Campa
            new MaestroData("Maestro Campa", new PokemonInfo[]{
                    new PokemonInfo("59", "Arcanine", 125),
                    new PokemonInfo("146", "Moltres", 140),
                    new PokemonInfo("6", "Charizard", 160)
            }),
            // Piso 5: Maestro Ricardo
            new MaestroData("Maestro Ricardo", new PokemonInfo[]{
                    new PokemonInfo("65", "Alakazam", 130),
                    new PokemonInfo("376", "Metagross", 150),
                    new PokemonInfo("150", "Mewtwo", 175)
            }),
            // Piso 6: Gran Maestro Víctor
            new MaestroData("Gran Maestro Víctor", new PokemonInfo[]{
                    new PokemonInfo("384", "Rayquaza", 160),
                    new PokemonInfo("483", "Dialga", 180),
                    new PokemonInfo("487", "Giratina", 200)
            })
    };

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

        tvDamageFloatEnemy = findViewById(R.id.tvDamageFloatEnemy);
        tvDamageFloatPlayer = findViewById(R.id.tvDamageFloatPlayer);

        btnAttack1 = findViewById(R.id.btnAttack1);
        btnAttack2 = findViewById(R.id.btnAttack2);
        btnAttack3 = findViewById(R.id.btnAttack3);
        btnAttack4 = findViewById(R.id.btnAttack4);
        btnSelectPokemon = findViewById(R.id.btnSelectPokemon);

        btnSelectPokemon.setOnClickListener(v -> promptRandomTeamSelection(this::resetBattleState));

        promptRandomTeamSelection(this::startTowerChallenge);
    }

    private PokemonMoveSet getPokemonMoveSet(String pokeId) {
        switch (pokeId) {
            case "25": // Pikachu
                return new PokemonMoveSet("Impactrueno", 24, false, "Rayo", 36, false, "Onda Certera", 32, true, "Trueno Devastador", 55, false);
            case "6": // Charizard
                return new PokemonMoveSet("Ascuas", 25, false, "Lanzallamas", 38, false, "Respiro", 35, true, "Envite Ígneo", 58, false);
            case "9": // Blastoise
                return new PokemonMoveSet("Pistola Agua", 22, false, "Hidrobomba", 36, false, "Refugio", 32, true, "Rayo Hielo", 52, false);
            case "3": // Venusaur
                return new PokemonMoveSet("Hoja Afilada", 24, false, "Látigo Cepa", 35, false, "Síntesis", 35, true, "Rayo Solar", 55, false);
            case "94": // Gengar
                return new PokemonMoveSet("Tinieblas", 24, false, "Bola Sombra", 38, false, "Come Sueños", 32, true, "Bomba Lodo", 54, false);
            case "150": // Mewtwo
                return new PokemonMoveSet("Confusión", 28, false, "Psíquico", 42, false, "Recuperación", 38, true, "Onda Mental", 62, false);
            case "448": // Lucario
                return new PokemonMoveSet("Ataque Rápido", 24, false, "Esfera Aural", 38, false, "Paz Mental", 30, true, "Abocajarro", 56, false);
            case "149": // Dragonite
                return new PokemonMoveSet("Carga Dragón", 26, false, "Garra Dragón", 40, false, "Danza Dragón", 32, true, "Hiperrayo", 58, false);
            case "130": // Gyarados
                return new PokemonMoveSet("Mordisco", 22, false, "Cascada", 36, false, "Acualivio", 28, true, "Hidroariete", 52, false);
            case "143": // Snorlax
                return new PokemonMoveSet("Golpe Cuerpo", 25, false, "Pisotón", 35, false, "Descanso", 40, true, "Gigaimpacto", 55, false);
            case "257": // Blaziken
                return new PokemonMoveSet("Doble Patada", 25, false, "Patada Ígnea", 38, false, "Corpulencia", 30, true, "Pájaro Osado", 56, false);
            case "254": // Sceptile
                return new PokemonMoveSet("Corte Furia", 22, false, "Hoja Aguda", 36, false, "Arraigo", 32, true, "Llama Dragón", 52, false);
            case "260": // Swampert
                return new PokemonMoveSet("Pistola Agua", 24, false, "Terremoto", 38, false, "Hidrocuración", 32, true, "Machada", 54, false);
            case "445": // Garchomp
                return new PokemonMoveSet("Bucle Dragón", 26, false, "Garra Dragón", 40, false, "Piel Tosca", 30, true, "Enfado", 58, false);
            case "131": // Lapras
                return new PokemonMoveSet("Canto Helado", 22, false, "Rayo Hielo", 36, false, "Canto Agua", 35, true, "Frío Polar", 54, false);
            default:
                return new PokemonMoveSet("Ataque Directo", 22, false, "Golpe Crítico", 35, false, "Recuperar HP", 30, true, "Ataque Final", 50, false);
        }
    }

    private void updateMoveButtons() {
        if (playerTeam.isEmpty()) return;
        PokemonInfo activePoke = playerTeam.get(currentPlayerPokemonIndex);
        PokemonMoveSet moves = getPokemonMoveSet(activePoke.id);

        btnAttack1.setText(moves.m1Name);
        btnAttack2.setText(moves.m2Name);
        btnAttack3.setText(moves.m3Name);
        btnAttack4.setText(moves.m4Name);

        btnAttack1.setOnClickListener(v -> executePlayerTurn(moves.m1Name, moves.m1Val, moves.m1IsHeal));
        btnAttack2.setOnClickListener(v -> executePlayerTurn(moves.m2Name, moves.m2Val, moves.m2IsHeal));
        btnAttack3.setOnClickListener(v -> executePlayerTurn(moves.m3Name, moves.m3Val, moves.m3IsHeal));
        btnAttack4.setOnClickListener(v -> executePlayerTurn(moves.m4Name, moves.m4Val, moves.m4IsHeal));
    }

    private void promptRandomTeamSelection(Runnable onTeamSelected) {
        List<String[]> poolCopy = new ArrayList<>(Arrays.asList(allAvailablePokemon));
        Collections.shuffle(poolCopy);

        String[][] random6 = new String[6][2];
        String[] optionNames = new String[6];
        for (int i = 0; i < 6; i++) {
            random6[i] = poolCopy.get(i);
            optionNames[i] = random6[i][1];
        }

        boolean[] checkedItems = new boolean[6];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona exactamente 3 Pokémon para tu equipo:");
        builder.setCancelable(false);
        builder.setMultiChoiceItems(optionNames, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("Confirmar Equipo (3)", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            List<Integer> selectedIndices = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (checkedItems[i]) {
                    selectedIndices.add(i);
                }
            }

            if (selectedIndices.size() != 3) {
                Toast.makeText(this, "Por favor elige exactamente 3 Pokémon (llevas " + selectedIndices.size() + ")", Toast.LENGTH_SHORT).show();
            } else {
                playerTeam.clear();
                for (int idx : selectedIndices) {
                    playerTeam.add(new PokemonInfo(random6[idx][0], random6[idx][1], 120));
                }
                dialog.dismiss();
                if (onTeamSelected != null) {
                    onTeamSelected.run();
                }
            }
        });
    }

    private void startTowerChallenge() {
        int floorFromIntent = getIntent().getIntExtra("FLOOR_INDEX", -1);
        if (floorFromIntent >= 0 && floorFromIntent < maestrosList.length) {
            floorIndex = floorFromIntent;
        } else {
            floorIndex = 0;
        }
        resetBattleState();
    }

    private void resetBattleState() {
        currentEnemyPokemonIndex = 0;
        currentPlayerPokemonIndex = 0;

        if (!playerTeam.isEmpty()) {
            playerMaxHp = playerTeam.get(0).hp;
            playerHp = playerMaxHp;
        }
        loadCurrentBattleState();
    }

    private void loadCurrentBattleState() {
        if (playerTeam.isEmpty()) return;

        MaestroData maestro = maestrosList[floorIndex];
        PokemonInfo enemyPoke = maestro.team[currentEnemyPokemonIndex];
        PokemonInfo playerPoke = playerTeam.get(currentPlayerPokemonIndex);

        enemyMaxHp = enemyPoke.hp;
        enemyHp = enemyMaxHp;

        playerMaxHp = playerPoke.hp;
        playerHp = playerMaxHp;

        tvFloorText.setText("PISO " + (floorIndex + 1) + " • " + (currentEnemyPokemonIndex + 1) + "/3 RIVAL");
        tvBossTitle.setText(maestro.nombre + "\n[" + enemyPoke.nombre + "]");
        tvPlayerName.setText("TU: " + playerPoke.nombre.toUpperCase() + " (" + (currentPlayerPokemonIndex + 1) + "/3)");

        updateHpBars();
        updateMoveButtons();

        String playerUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + playerPoke.id + ".png";
        String enemyUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + enemyPoke.id + ".png";

        Glide.with(this).load(playerUrl).into(ivPlayerSprite);
        Glide.with(this).load(enemyUrl).into(ivEnemySprite);

        tvBattleLog.setText("¡Combate en el Piso " + (floorIndex + 1) + "! " + maestro.nombre + " vs Tu equipo.");
        isPlayerTurn = true;
        setButtonsEnabled(true);
    }

    private void executePlayerTurn(String attackName, int value, boolean isHeal) {
        if (!isPlayerTurn || playerTeam.isEmpty()) return;

        setButtonsEnabled(false);
        PokemonInfo playerPoke = playerTeam.get(currentPlayerPokemonIndex);

        if (isHeal) {
            int healAmount = Math.abs(value);
            playerHp = Math.min(playerMaxHp, playerHp + healAmount);
            animateFloatingNumber(tvDamageFloatPlayer, "+" + healAmount + " HP", "#10B981");
            tvBattleLog.setText(playerPoke.nombre + " usó " + attackName + " (+ " + healAmount + " HP)!");
            updateHpBars();
        } else {
            animateAttackLunge(ivPlayerSprite, true);
            animateHitImpact(ivEnemySprite);

            enemyHp = Math.max(0, enemyHp - value);
            animateFloatingNumber(tvDamageFloatEnemy, "-" + value + " HP", "#EF4444");
            tvBattleLog.setText(playerPoke.nombre + " usó " + attackName + " e hizo " + value + " de daño!");
            updateHpBars();
        }

        if (enemyHp <= 0) {
            MaestroData maestro = maestrosList[floorIndex];
            PokemonInfo defeatedEnemy = maestro.team[currentEnemyPokemonIndex];

            if (currentEnemyPokemonIndex < 2) {
                currentEnemyPokemonIndex++;
                PokemonInfo nextEnemy = maestro.team[currentEnemyPokemonIndex];
                tvBattleLog.setText("¡Derrotaste a " + defeatedEnemy.nombre + "! " + maestro.nombre + " saca a " + nextEnemy.nombre + "...");
                new Handler(Looper.getMainLooper()).postDelayed(this::loadNextEnemyPokemon, 1400);
            } else {
                // ¡DERROTADOS LOS 3 POKÉMON DEL MAESTRO!
                SharedPreferences.Editor editor = getSharedPreferences("PokeTorrePrefs", Context.MODE_PRIVATE).edit();
                editor.putBoolean("derrotado_piso_" + floorIndex, true);
                editor.apply();

                tvBattleLog.setText("¡HAS DERROTADO A LOS 3 POKÉMON DE " + maestro.nombre.toUpperCase() + "!");
                new Handler(Looper.getMainLooper()).postDelayed(() -> showMedalVictoryDialog(maestro.nombre, floorIndex), 1000);
            }
            return;
        }

        isPlayerTurn = false;
        new Handler(Looper.getMainLooper()).postDelayed(this::executeAiTurn, 1400);
    }

    private void loadNextEnemyPokemon() {
        MaestroData maestro = maestrosList[floorIndex];
        PokemonInfo currentEnemy = maestro.team[currentEnemyPokemonIndex];

        enemyMaxHp = currentEnemy.hp;
        enemyHp = enemyMaxHp;

        tvFloorText.setText("PISO " + (floorIndex + 1) + " • " + (currentEnemyPokemonIndex + 1) + "/3 RIVAL");
        tvBossTitle.setText(maestro.nombre + "\n[" + currentEnemy.nombre + "]");

        updateHpBars();

        String enemyUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + currentEnemy.id + ".png";
        Glide.with(this).load(enemyUrl).into(ivEnemySprite);

        tvBattleLog.setText("¡" + maestro.nombre + " envió a " + currentEnemy.nombre + " (" + (currentEnemyPokemonIndex + 1) + "/3)!");
        isPlayerTurn = true;
        setButtonsEnabled(true);
    }

    private void executeAiTurn() {
        if (playerTeam.isEmpty()) return;

        MaestroData maestro = maestrosList[floorIndex];
        PokemonInfo currentEnemy = maestro.team[currentEnemyPokemonIndex];

        int damage = 14 + random.nextInt(15) + (floorIndex * 3);

        animateAttackLunge(ivEnemySprite, false);
        animateHitImpact(ivPlayerSprite);

        playerHp = Math.max(0, playerHp - damage);
        animateFloatingNumber(tvDamageFloatPlayer, "-" + damage + " HP", "#EF4444");

        updateHpBars();

        PokemonInfo currentPlayerPoke = playerTeam.get(currentPlayerPokemonIndex);
        tvBattleLog.setText("¡" + currentEnemy.nombre + " de " + maestro.nombre + " contraatacó e hizo " + damage + " de daño!");

        if (playerHp <= 0) {
            if (currentPlayerPokemonIndex < 2) {
                currentPlayerPokemonIndex++;
                PokemonInfo nextPlayerPoke = playerTeam.get(currentPlayerPokemonIndex);
                tvBattleLog.setText("¡" + currentPlayerPoke.nombre + " se debilitó! ¡Entra " + nextPlayerPoke.nombre + "!");
                new Handler(Looper.getMainLooper()).postDelayed(this::loadNextPlayerPokemon, 1400);
            } else {
                tvBattleLog.setText("¡Has sido derrotado por " + maestro.nombre + "!");
                showGameOverDialog();
            }
        } else {
            isPlayerTurn = true;
            setButtonsEnabled(true);
        }
    }

    private void loadNextPlayerPokemon() {
        PokemonInfo playerPoke = playerTeam.get(currentPlayerPokemonIndex);
        playerMaxHp = playerPoke.hp;
        playerHp = playerMaxHp;

        tvPlayerName.setText("TU: " + playerPoke.nombre.toUpperCase() + " (" + (currentPlayerPokemonIndex + 1) + "/3)");
        updateHpBars();
        updateMoveButtons();

        String playerUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/back/" + playerPoke.id + ".png";
        Glide.with(this).load(playerUrl).into(ivPlayerSprite);

        isPlayerTurn = true;
        setButtonsEnabled(true);
    }

    private void animateFloatingNumber(TextView tvFloat, String text, String colorHex) {
        tvFloat.setText(text);
        tvFloat.setTextColor(Color.parseColor(colorHex));
        tvFloat.setVisibility(View.VISIBLE);
        tvFloat.setAlpha(1.0f);
        tvFloat.setTranslationY(0f);

        ObjectAnimator moveUp = ObjectAnimator.ofFloat(tvFloat, "translationY", 0f, -80f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(tvFloat, "alpha", 1.0f, 0.0f);

        moveUp.setDuration(1100);
        fadeOut.setDuration(1100);

        moveUp.start();
        fadeOut.start();
    }

    private void animateAttackLunge(ImageView attacker, boolean isPlayer) {
        float lungeDistance = isPlayer ? 45f : -45f;
        ObjectAnimator lunge = ObjectAnimator.ofFloat(attacker, "translationX", 0f, lungeDistance, 0f);
        lunge.setDuration(260);
        lunge.start();
    }

    private void animateHitImpact(ImageView target) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(target, "translationX", 0f, -18f, 18f, -12f, 12f, -6f, 6f, 0f);
        ObjectAnimator flash = ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.15f, 1.0f, 0.15f, 1.0f);
        shake.setDuration(420);
        flash.setDuration(420);
        shake.start();
        flash.start();
    }

    private void showGameOverDialog() {
        MaestroData maestro = maestrosList[floorIndex];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💀 ¡DERROTADO EN LA TORRE!");
        builder.setMessage("Tu equipo fue superado por " + maestro.nombre + " en el Piso " + (floorIndex + 1) + ".\n\n" +
                "¡No te rindas! Ajusta tu estrategia, selecciona tus mejores Pokémon y vuelve a intentarlo.");
        builder.setPositiveButton("🔥 REINTENTAR COMBATE", (dialog, which) -> resetBattleState());
        builder.setNegativeButton("🚪 RETIRARSE", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    private void showMedalVictoryDialog(String maestroNombre, int floor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_medalla_gran_torre, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        TextView tvName = dialogView.findViewById(R.id.tvMedalMaestroName);
        TextView tvDesc = dialogView.findViewById(R.id.tvMedalDescription);
        Button btnClaim = dialogView.findViewById(R.id.btnClaimMedal);

        if (tvName != null) {
            tvName.setText("¡VICTORIA ÉPICA!");
        }
        if (tvDesc != null) {
            tvDesc.setText("¡Has derrotado a los 3 Pokémon de " + maestroNombre + " en el Piso " + (floor + 1) + "!\nConseguiste su Medalla Oficial de la Torre.");
        }
        if (btnClaim != null) {
            btnClaim.setText("¡RECLAMAR MEDALLA Y CONTINUAR!");
            btnClaim.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });
        }

        dialog.show();
    }

    private void updateHpBars() {
        pbPlayerHp.setMax(playerMaxHp);
        pbPlayerHp.setProgress(playerHp);

        pbEnemyHp.setMax(enemyMaxHp);
        pbEnemyHp.setProgress(enemyHp);

        tvPlayerHpText.setText(playerHp + "/" + playerMaxHp + " HP");
        tvEnemyHpText.setText(enemyHp + "/" + enemyMaxHp + " HP");
    }

    private void setButtonsEnabled(boolean enabled) {
        btnAttack1.setEnabled(enabled);
        btnAttack2.setEnabled(enabled);
        btnAttack3.setEnabled(enabled);
        btnAttack4.setEnabled(enabled);
    }
}