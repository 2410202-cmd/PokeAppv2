package com.example.pokeappv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainMenuActivity extends AppCompatActivity {

    private VideoView bgVideoView;
    private TextView tvUserEmail, tvSubTitle;
    private ImageButton btnLogout;
    private ImageView ivTrainerAvatar;
    private FirebaseAuth mAuth;

    private LinearLayout cardPokedex, cardBattleEmulator, cardBattleVersus, cardTorrePokemon;
    private LinearLayout cardQuienEsEsePokemon, cardSafariPokemon, cardMaestroTipos, cardPokeMemory;
    private LinearLayout cardFavoritos, cardHistorial, cardMedallas, cardConfiguracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mAuth = FirebaseAuth.getInstance();

        // 1. Vincular componentes de la interfaz
        initViews();

        // 2. Configurar reproducción de video de fondo
        setupVideoView();

        // 3. Obtener y mostrar datos del usuario desde Firebase
        setupUserData();

        // 4. Aplicar efectos estéticos y fuentes
        applyFireEffectAndFont(tvUserEmail);
        applyFireEffectAndFont(tvSubTitle);

        // 5. Configurar avatar del entrenador con Glide (Sprite GIF animado)
        setupTrainerAvatar();

        // 6. Configurar evento de cierre de sesión
        setupLogoutButton();

        // 7. Configurar navegaciones y eventos de clic en el grid de tarjetas
        setupMenuClickListeners();
    }

    private void initViews() {
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvSubTitle = findViewById(R.id.tvSubTitle);
        btnLogout = findViewById(R.id.btnLogout);
        ivTrainerAvatar = findViewById(R.id.ivTrainerAvatar);

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
        cardConfiguracion = findViewById(R.id.cardConfiguracion);
    }

    private void setupVideoView() {
        bgVideoView = findViewById(R.id.bgVideoView);
        if (bgVideoView != null) {
            String path = "android.resource://" + getPackageName() + "/" + R.raw.menu;

            bgVideoView.post(() -> {
                bgVideoView.setVideoURI(Uri.parse(path));

                bgVideoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    mp.setVolume(0f, 0f); // Silenciar el audio del video
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    bgVideoView.start();
                });

                bgVideoView.setOnErrorListener((mp, what, extra) -> {
                    try {
                        String backupPath = "android.resource://" + getPackageName() + "/" + R.raw.inicio;
                        bgVideoView.setVideoURI(Uri.parse(backupPath));
                        bgVideoView.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                });
            });
        }
    }

    private void setupUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && tvUserEmail != null) {
            String email = currentUser.getEmail();
            String username = (email != null && email.contains("@")) ? email.split("@")[0] : "Entrenador";
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                username = currentUser.getDisplayName();
            }
            tvUserEmail.setText("¡Hola, " + username + "!");
        }
    }

    private void applyFireEffectAndFont(TextView textView) {
        if (textView == null) return;

        try {
            textView.setTypeface(ResourcesCompat.getFont(this, R.font.pokemon_font));
        } catch (Exception e) {
            e.printStackTrace();
        }

        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        textView.post(() -> {
            int textHeight = textView.getHeight();
            if (textHeight <= 0) textHeight = (int) textView.getTextSize();

            Shader fireShader = new LinearGradient(
                    0, 0, 0, textHeight,
                    new int[]{
                            Color.parseColor("#FFFF00"), // Amarillo Llama
                            Color.parseColor("#FF6600"), // Naranja Fuego
                            Color.parseColor("#DD0000")  // Rojo Fuego
                    },
                    new float[]{0f, 0.45f, 1f},
                    Shader.TileMode.CLAMP
            );

            textView.getPaint().setShader(fireShader);
            textView.setShadowLayer(14f, 0f, 2f, Color.parseColor("#FF2200"));
            textView.invalidate();
        });
    }

    private void setupTrainerAvatar() {
        if (ivTrainerAvatar != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/25.gif")
                    .into(ivTrainerAvatar);

            ivTrainerAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(MainMenuActivity.this, PerfilActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupLogoutButton() {
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(MainMenuActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }
    }

    private void setupMenuClickListeners() {
        // Pokédex: Carga inicial directa en ID #1 (Bulbasaur)
        if (cardPokedex != null) {
            cardPokedex.setOnClickListener(v -> {
                cardPokedex.setEnabled(false);
                animateCardClick(v, () -> {
                    cardPokedex.setEnabled(true);
                    Intent intent = new Intent(MainMenuActivity.this, PokedexActivity.class);
                    intent.putExtra("POKEMON_NAME_OR_ID", "1");
                    startActivity(intent);
                });
            });
        }

        // Navegación modular a las actividades principales
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
        setupCardNavigation(cardConfiguracion, ConfiguracionActivity.class);
    }

    private void setupCardNavigation(View cardView, Class<?> targetActivity) {
        if (cardView != null) {
            cardView.setOnClickListener(v -> {
                cardView.setEnabled(false);
                animateCardClick(v, () -> {
                    cardView.setEnabled(true);
                    try {
                        Intent intent = new Intent(MainMenuActivity.this, targetActivity);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(MainMenuActivity.this, "Módulo en desarrollo", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void animateCardClick(View view, Runnable onAnimationEnd) {
        view.animate()
                .scaleX(0.94f)
                .scaleY(0.94f)
                .setDuration(80)
                .withEndAction(() -> view.animate()
                        .scaleX(1.02f)
                        .scaleY(1.02f)
                        .setDuration(80)
                        .withEndAction(() -> view.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(60)
                                .withEndAction(onAnimationEnd)
                                .start())
                        .start())
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgVideoView != null && !bgVideoView.isPlaying()) {
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