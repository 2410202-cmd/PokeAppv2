package com.example.pokeappv2;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvLoginTitle, tvGoToRegister, tvForgotPassword;
    private ImageView ivLoginPokeballGif;
    private VideoView bgVideoLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pantalla completa Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // 1. Vincular componentes
        bgVideoLogin = findViewById(R.id.bgVideoLogin);
        ivLoginPokeballGif = findViewById(R.id.ivLoginPokeballGif);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // 2. PLAN B: Cargar Pokébola / GIF de Pikachu rodando con Glide + Animación
        if (ivLoginPokeballGif != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/25.gif")
                    .into(ivLoginPokeballGif);

            // Efecto de rotación suave continua por si acaso
            RotateAnimation rotate = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            rotate.setDuration(3000);
            rotate.setRepeatCount(Animation.INFINITE);
            ivLoginPokeballGif.startAnimation(rotate);
        }

        // 3. Aplicar fuente
        try {
            Typeface pokemonFont = ResourcesCompat.getFont(this, R.font.pokemon_font);
            if (pokemonFont != null) {
                tvLoginTitle.setTypeface(pokemonFont);
                btnLogin.setTypeface(pokemonFont);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. Video de fondo
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.inicio);
        bgVideoLogin.setVideoURI(videoUri);
        bgVideoLogin.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            bgVideoLogin.start();
        });

        // 5. Iniciar Sesión
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Error";
                            Toast.makeText(LoginActivity.this, "Error: " + err, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 6. Ir a Registro
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 7. Olvidó contraseña
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgVideoLogin != null && !bgVideoLogin.isPlaying()) {
            bgVideoLogin.start();
        }
    }
}