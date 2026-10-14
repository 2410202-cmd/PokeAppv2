package com.example.pokeappv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MedallasActivity extends AppCompatActivity {

    private TextView tvHistorialMedallas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medallas);

        Button btnBadge1 = findViewById(R.id.btnBadge1);
        Button btnBadge2 = findViewById(R.id.btnBadge2);
        Button btnBadge3 = findViewById(R.id.btnBadge3);

        tvHistorialMedallas = findViewById(R.id.tvHistorialMedallas);

        if (btnBadge1 != null) {
            btnBadge1.setOnClickListener(v -> Toast.makeText(this, "🪨 Ganada a Brock en Gimnasio Plateada", Toast.LENGTH_SHORT).show());
        }
        if (btnBadge2 != null) {
            btnBadge2.setOnClickListener(v -> Toast.makeText(this, "💧 Ganada a Misty en Gimnasio Celeste", Toast.LENGTH_SHORT).show());
        }
        if (btnBadge3 != null) {
            btnBadge3.setOnClickListener(v -> Toast.makeText(this, "🔒 Derrota a Lt. Surge para desbloquear", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHistorialMedallasTorre();
    }

    private void cargarHistorialMedallasTorre() {
        if (tvHistorialMedallas != null) {
            SharedPreferences medalPrefs = getSharedPreferences("PokeMedallasHistorial", Context.MODE_PRIVATE);
            String historial = medalPrefs.getString("historial_list", "");

            if (!historial.isEmpty()) {
                tvHistorialMedallas.setText("• " + historial);
            } else {
                tvHistorialMedallas.setText("Aún no has conquistado la Torre de Combate.\n¡Derrota a todos los 7 Maestros (incluyendo a Badi) para ganar tu primera Medalla Única y guardarla en tu historial!");
            }
        }
    }
}