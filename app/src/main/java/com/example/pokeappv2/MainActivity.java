package com.example.pokeappv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private VideoView bgVideoView;
    private TextView tvUserEmail, tvSubTitle;
    private ImageButton btnLogout;
    private ImageView ivTrainerAvatar;
    private FirebaseAuth mAuth;

    private LinearLayout cardPokedex, cardBattleEmulator, cardBattleVersus, cardTorrePokemon;
    private LinearLayout cardQuienEsEsePokemon, cardSafariPokemon, cardMaestroTipos, cardPokeMemory;
    private LinearLayout cardFavoritos, cardHistorial, cardMedallas, cardMiPerfil, cardConfiguracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // 1. Vincular componentes
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvSubTitle = findViewById(R.id.tvSubTitle);
        btnLogout = findViewById(R.id.btnLogout);
        ivTrainerAvatar = findViewById(R.id.ivTrainerAvatar);

        // 2. Configurar y Reproducir el Vídeo (menu.mp4)
        setupVideoView();

        // 3. Obtener Datos del usuario
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && tvUserEmail != null) {
            String email = currentUser.getEmail();
            String username = (email != null && email.contains("@")) ? email.split("@")[0] : "Entrenador";
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                username = currentUser.getDisplayName();
            }
            tvUserEmail.setText("¡Hola, " + username + "!");
        }

        // 4. Aplicar Fuente Personalizada + Efecto de Llamas de Fuego (DESPUÉS de cambiar el texto)
        applyFireEffectAndFont(tvUserEmail);
        applyFireEffectAndFont(tvSubTitle);

        // 5. Cargar Avatar GIF con Glide
        if (ivTrainerAvatar != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/25.gif")
                    .into(ivTrainerAvatar);
        }

        // 6. Botón Cerrar Sesión
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }

        // 7. Inicializar Menús y Escuchadores
        initMenuCards();
        setupMenuClickListeners();
    }

    private void setupVideoView() {
        bgVideoView = findViewById(R.id.bgVideoView);
        if (bgVideoView != null) {
            String path = "android.resource://" + getPackageName() + "/" + R.raw.menu;

            bgVideoView.post(() -> {
                bgVideoView.setVideoPath(path);

                bgVideoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    mp.setVolume(0f, 0f); // Silenciar audio
                    mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    bgVideoView.start();
                });

                bgVideoView.setOnErrorListener((mp, what, extra) -> {
                    String backupPath = "android.resource://" + getPackageName() + "/" + R.raw.inicio;
                    bgVideoView.setVideoPath(backupPath);
                    bgVideoView.start();
                    return true;
                });
            });
        }
    }

    private void applyFireEffectAndFont(TextView textView) {
        if (textView == null) return;

        // Cargar fuente desde res/font/pokemon_font.ttf
        try {
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.pokemon_font));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Desactivar aceleración por hardware local para permitir el dibujado correcto del degradado y sombra
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        textView.post(() -> {
            int textHeight = textView.getHeight();
            if (textHeight <= 0) textHeight = (int) textView.getTextSize();

            Shader fireShader = new LinearGradient(
                    0, 0, 0, textHeight,
                    new int[]{
                            Color.parseColor("#FFFF00"), // Amarillo Llama (Arriba)
                            Color.parseColor("#FF6600"), // Naranja Fuego (Medio)
                            Color.parseColor("#DD0000")  // Rojo Fuego (Abajo)
                    },
                    new float[]{0f, 0.45f, 1f},
                    Shader.TileMode.CLAMP
            );

            textView.getPaint().setShader(fireShader);
            textView.setShadowLayer(14f, 0f, 2f, Color.parseColor("#FF2200"));
            textView.invalidate();
        });
    }

    private void initMenuCards() {
        cardPokedex = findViewById(R.id.cardPokedex);
        cardBattleEmulator = findViewById(R.id.cardBattleEmulator);
        cardBattleVersus = findViewById(R.id.cardBattleVersus);
        cardQuienEsEsePokemon = findViewById(R.id.cardQuienEsEsePokemon);
        cardTorrePokemon = findViewById(R.id.cardTorrePokemon);
        cardSafariPokemon = findViewById(R.id.cardSafariPokemon);
        cardMaestroTipos = findViewById(R.id.cardMaestroTipos);
        cardPokeMemory = findViewById(R.id.cardPokeMemory);
        cardFavoritos = findViewById(R.id.cardFavoritos);
        cardHistorial = findViewById(R.id.cardHistorial);
        cardMedallas = findViewById(R.id.cardMedallas);
        cardMiPerfil = findViewById(R.id.cardMiPerfil);
        cardConfiguracion = findViewById(R.id.cardConfiguracion);
    }

    private void setupMenuClickListeners() {
        setupCardNavigation(cardPokedex, PokedexActivity.class);
        setupCardNavigation(cardBattleEmulator, BattleEmulatorActivity.class);
        setupCardNavigation(cardBattleVersus, BattleVersusActivity.class);
        setupCardNavigation(cardQuienEsEsePokemon, QuienEsPokemonActivity.class);
        setupCardNavigation(cardTorrePokemon, TorrePokemonActivity.class);
        setupCardNavigation(cardSafariPokemon, ZonaSafariActivity.class);
        setupCardNavigation(cardMaestroTipos, MaestroTiposActivity.class);
        setupCardNavigation(cardPokeMemory, TorrePokemonActivity.class);
        setupCardNavigation(cardFavoritos, FavoritosActivity.class);
        setupCardNavigation(cardHistorial, HistorialActivity.class);
        setupCardNavigation(cardMedallas, MedallasActivity.class);
        setupCardNavigation(cardMiPerfil, PerfilActivity.class);
        setupCardNavigation(cardConfiguracion, ConfiguracionActivity.class);
    }

    private void setupCardNavigation(View cardView, Class<?> targetActivity) {
        if (cardView != null) {
            cardView.setOnClickListener(v -> {
                cardView.setEnabled(false);
                animateCardClick(v, () -> {
                    cardView.setEnabled(true);
                    Intent intent = new Intent(MainActivity.this, targetActivity);
                    startActivity(intent);
                });
            });
        }
    }

    private void animateCardClick(View view, Runnable onAnimationEnd) {
        view.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .alpha(0.85f)
                .setDuration(90)
                .withEndAction(() -> view.animate()
                        .scaleX(1.04f)
                        .scaleY(1.04f)
                        .alpha(1.0f)
                        .setDuration(90)
                        .withEndAction(() -> view.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(70)
                                .withEndAction(onAnimationEnd)
                                .start())
                        .start())
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgVideoView != null) {
            bgVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bgVideoView != null && bgVideoView.isPlaying()) {
            bgVideoView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bgVideoView != null) {
            bgVideoView.stopPlayback();
        }
    }
}