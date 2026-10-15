package com.example.pokeappv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView ivProfileAvatar;
    private TextView tvProfileEmail;
    private Button btnLogout;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();

        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && tvProfileEmail != null) {
            tvProfileEmail.setText(currentUser.getEmail());
        }

        if (ivProfileAvatar != null) {
            Glide.with(this)
                    .asGif()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/25.gif")
                    .into(ivProfileAvatar);
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                mAuth.signOut();
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
