package com.example.pokeappv2;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private Button btnRegisterUser;
    private TextView tvRegisterTitle, tvBackToLogin;
    private ImageView ivRegisterGif;
    private VideoView bgVideoRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // 1. Vincular componentes
        bgVideoRegister = findViewById(R.id.bgVideoRegister);
        ivRegisterGif = findViewById(R.id.ivRegisterGif);
        tvRegisterTitle = findViewById(R.id.tvRegisterTitle);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegisterUser = findViewById(R.id.btnRegisterUser);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // 2. Aplicar fuente Pokémon
        try {
            Typeface pokemonFont = ResourcesCompat.getFont(this, R.font.pokemon_font);
            if (pokemonFont != null) {
                tvRegisterTitle.setTypeface(pokemonFont);
                btnRegisterUser.setTypeface(pokemonFont);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. Configurar y reproducir video de fondo (res/raw/inicio.mp4)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.inicio);
        bgVideoRegister.setVideoURI(videoUri);
        bgVideoRegister.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            bgVideoRegister.start();
        });

        // 4. Cargar GIF de Pokémon con Glide (Charmander)
        if (ivRegisterGif != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/4.gif")
                    .into(ivRegisterGif);
        }

        // 5. Evento del botón Registrarme
        btnRegisterUser.setOnClickListener(v -> {
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear cuenta en Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "¡Entrenador registrado con éxito!", Toast.LENGTH_SHORT).show();

                            // REDIRECCIÓN A MainMenuActivity (El menú principal renovado)
                            Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Error en el registro";
                            Toast.makeText(RegisterActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 6. Evento para volver al Login
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgVideoRegister != null && !bgVideoRegister.isPlaying()) {
            bgVideoRegister.start();
        }
    }
}