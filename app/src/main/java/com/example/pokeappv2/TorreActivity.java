package com.example.pokeappv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TorreActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torre);

        prefs = getSharedPreferences("PokeTorrePrefs", Context.MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarEstadoPisos();
    }

    private void actualizarEstadoPisos() {
        boolean p0Derrotado = prefs.getBoolean("derrotado_piso_0", false);
        boolean p1Derrotado = prefs.getBoolean("derrotado_piso_1", false);
        boolean p2Derrotado = prefs.getBoolean("derrotado_piso_2", false);
        boolean p3Derrotado = prefs.getBoolean("derrotado_piso_3", false);
        boolean p4Derrotado = prefs.getBoolean("derrotado_piso_4", false);
        boolean p5Derrotado = prefs.getBoolean("derrotado_piso_5", false);

        configurarPiso(R.id.cardPiso1, R.id.tvStatusPiso1, 0, "Maestro Víctor", true, p0Derrotado);
        configurarPiso(R.id.cardPiso2, R.id.tvStatusPiso2, 1, "Maestro Everardo", p0Derrotado, p1Derrotado);
        configurarPiso(R.id.cardPiso3, R.id.tvStatusPiso3, 2, "Maestro Edgar", p1Derrotado, p2Derrotado);
        configurarPiso(R.id.cardPiso4, R.id.tvStatusPiso4, 3, "Maestro Campa", p2Derrotado, p3Derrotado);
        configurarPiso(R.id.cardPiso5, R.id.tvStatusPiso5, 4, "Maestro Ricardo", p3Derrotado, p4Derrotado);
        configurarPiso(R.id.cardPiso6, R.id.tvStatusPiso6, 5, "Gran Maestro Víctor", p4Derrotado, p5Derrotado);
    }

    private void configurarPiso(int cardId, int tvStatusId, int floorIndex, String maestroNombre, boolean estaDesbloqueado, boolean estaDerrotado) {
        CardView card = findViewById(cardId);
        TextView tvStatus = findViewById(tvStatusId);

        if (tvStatus != null) {
            if (estaDerrotado) {
                tvStatus.setText("🏅 GANADA");
                tvStatus.setTextColor(Color.parseColor("#FFD700"));
            } else if (estaDesbloqueado) {
                tvStatus.setText("⚔️ PELEAR");
                tvStatus.setTextColor(Color.parseColor("#FF5722"));
            } else {
                tvStatus.setText("🔒 BLOQUEADO");
                tvStatus.setTextColor(Color.parseColor("#64748B"));
            }
        }

        if (card != null) {
            if (estaDerrotado) {
                card.setAlpha(0.85f);
                card.setOnClickListener(v -> Toast.makeText(this, "¡Ya derrotaste a " + maestroNombre + " y ganaste su Medalla!", Toast.LENGTH_SHORT).show());
            } else if (estaDesbloqueado) {
                card.setAlpha(1.0f);
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, TorrePokemonActivity.class);
                    intent.putExtra("FLOOR_INDEX", floorIndex);
                    startActivity(intent);
                });
            } else {
                card.setAlpha(0.45f);
                card.setOnClickListener(v -> Toast.makeText(this, "¡Debes derrotar al Maestro del Piso anterior para desbloquear este nivel!", Toast.LENGTH_SHORT).show());
            }
        }
    }
}