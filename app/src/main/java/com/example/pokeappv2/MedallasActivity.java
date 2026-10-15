package com.example.pokeappv2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MedallasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medallas);

        Button btnBadge1 = findViewById(R.id.btnBadge1);
        Button btnBadge2 = findViewById(R.id.btnBadge2);
        Button btnBadge3 = findViewById(R.id.btnBadge3);

        btnBadge1.setOnClickListener(v -> Toast.makeText(this, "🪨 Ganada a Brock en Gimnasio Plateada", Toast.LENGTH_SHORT).show());
        btnBadge2.setOnClickListener(v -> Toast.makeText(this, "💧 Ganada a Misty en Gimnasio Celeste", Toast.LENGTH_SHORT).show());
        btnBadge3.setOnClickListener(v -> Toast.makeText(this, "🔒 Derrota a Lt. Surge para desbloquear", Toast.LENGTH_SHORT).show());
    }
}