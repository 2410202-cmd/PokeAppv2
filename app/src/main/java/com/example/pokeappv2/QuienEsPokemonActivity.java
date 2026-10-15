package com.example.pokeappv2;

import android.animation.ObjectAnimator;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Random;

public class QuienEsPokemonActivity extends AppCompatActivity {

    private ImageView ivSilhouette;
    private View vGlowRing;
    private EditText etGuessName;
    private Button btnCheckGuess, btnSkip;
    private TextView tvScore, tvStreak, tvRevealedName;

    private int currentPokemonId;
    private String currentPokemonName = "";
    private int score = 0;
    private int streak = 0;
    private boolean isRevealed = false;

    // Banco de Pokémon para el juego
    private final String[][] pokemonList = {
            {"1", "bulbasaur"}, {"4", "charmander"}, {"7", "squirtle"},
            {"25", "pikachu"}, {"39", "jigglypuff"}, {"54", "psyduck"},
            {"94", "gengar"}, {"130", "gyarados"}, {"143", "snorlax"},
            {"149", "dragonite"}, {"150", "mewtwo"}, {"151", "mew"}
    };

    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quien_es_pokemon);

        ivSilhouette = findViewById(R.id.ivSilhouette);
        vGlowRing = findViewById(R.id.vGlowRing);
        etGuessName = findViewById(R.id.etGuessName);
        btnCheckGuess = findViewById(R.id.btnCheckGuess);
        btnSkip = findViewById(R.id.btnSkip);
        tvScore = findViewById(R.id.tvScore);
        tvStreak = findViewById(R.id.tvStreak);
        tvRevealedName = findViewById(R.id.tvRevealedName);

        loadNewPokemon();

        btnCheckGuess.setOnClickListener(v -> checkAnswer());
        btnSkip.setOnClickListener(v -> skipPokemon());
    }

    private void loadNewPokemon() {
        isRevealed = false;
        tvRevealedName.setText("¿ ? ?");
        tvRevealedName.setTextColor(Color.parseColor("#38BDF8"));

        int index = random.nextInt(pokemonList.length);
        currentPokemonId = Integer.parseInt(pokemonList[index][0]);
        currentPokemonName = pokemonList[index][1];

        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + currentPokemonId + ".png";

        // MODO SILUETA: Pinta la imagen completamente de negro
        ivSilhouette.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);

        // Carga con Glide
        Glide.with(this).load(spriteUrl).into(ivSilhouette);
        etGuessName.setText("");

        // Efecto de entrada por escala
        ivSilhouette.setScaleX(0.3f);
        ivSilhouette.setScaleY(0.3f);
        ivSilhouette.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void checkAnswer() {
        if (isRevealed) return;

        String userGuess = etGuessName.getText().toString().trim().toLowerCase();

        if (userGuess.equalsIgnoreCase(currentPokemonName)) {
            // ACIERTO!
            isRevealed = true;
            score += 10 + (streak * 2); // Bonus por racha
            streak++;

            tvScore.setText("SCORE: " + score);
            tvStreak.setText("🔥 Racha: " + streak);
            tvRevealedName.setText("¡ES " + currentPokemonName.toUpperCase() + "!");
            tvRevealedName.setTextColor(Color.parseColor("#22C55E"));

            // EFECTO VISUAL DE REVELACIÓN (Animación Neón)
            ivSilhouette.clearColorFilter(); // Quita la silueta negra y muestra el color real

            // Animación Pulse / POP al acertar
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivSilhouette, "scaleX", 1.0f, 1.25f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivSilhouette, "scaleY", 1.0f, 1.25f, 1.0f);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            scaleX.start();
            scaleY.start();

            // Pasa al siguiente Pokémon tras 2 segundos
            ivSilhouette.postDelayed(this::loadNewPokemon, 2000);

        } else {
            // ERROR
            streak = 0;
            tvStreak.setText("🔥 Racha: 0");
            Toast.makeText(this, "❌ ¡No es ese Pokémon! Inténtalo de nuevo", Toast.LENGTH_SHORT).show();

            // Animación de Vibración/Sacudida
            ivSilhouette.animate()
                    .translationXBy(20f)
                    .setDuration(50)
                    .withEndAction(() -> ivSilhouette.animate().translationXBy(-40f).setDuration(50).withEndAction(() -> ivSilhouette.setTranslationX(0f)))
                    .start();
        }
    }

    private void skipPokemon() {
        streak = 0;
        tvStreak.setText("🔥 Racha: 0");
        Toast.makeText(this, "Era " + currentPokemonName.toUpperCase(), Toast.LENGTH_SHORT).show();
        loadNewPokemon();
    }
}