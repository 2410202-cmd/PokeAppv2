package com.example.pokeappv2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3200; // 3.2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ocultar barra superior para hacerlo Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        ImageView ivSplashLogo = findViewById(R.id.ivSplashLogo);
        TextView tvSplashTitle = findViewById(R.id.tvSplashTitle);

        // 1. Cargar el GIF de Gengar animado con Glide
        if (ivSplashLogo != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/94.gif")
                    .into(ivSplashLogo);

            // Animación suave de flotación
            try {
                Animation pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse_glow);
                ivSplashLogo.startAnimation(pulseAnim);
            } catch (Exception ignored) {}
        }

        // 2. Aplicar estilo neón/degradado a las letras del título
        if (tvSplashTitle != null) {
            try {
                tvSplashTitle.setTypeface(ResourcesCompat.getFont(this, R.font.pokemon_font));
            } catch (Exception ignored) {}

            tvSplashTitle.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            tvSplashTitle.post(() -> {
                int height = tvSplashTitle.getHeight();
                if (height <= 0) height = (int) tvSplashTitle.getTextSize();

                // Degradado Fuego Neón (Amarillo Creador -> Cian Neón -> Azul)
                Shader textShader = new LinearGradient(
                        0, 0, 0, height,
                        new int[]{
                                Color.parseColor("#FFE600"),
                                Color.parseColor("#38BDF8"),
                                Color.parseColor("#0284C7")
                        },
                        new float[]{0f, 0.5f, 1f},
                        Shader.TileMode.CLAMP
                );

                tvSplashTitle.getPaint().setShader(textShader);
                tvSplashTitle.setShadowLayer(16f, 0f, 4f, Color.parseColor("#00E5FF"));
                tvSplashTitle.invalidate();
            });
        }

        // 3. Transición a LoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}