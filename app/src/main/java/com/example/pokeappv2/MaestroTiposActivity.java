package com.example.pokeappv2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MaestroTiposActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maestro_tipos);

        Button btnProbar = findViewById(R.id.btnProbartipo);
        btnProbar.setOnClickListener(v ->
                Toast.makeText(this, "Agua vs Fuego: ¡Super eficaz x2!", Toast.LENGTH_SHORT).show()
        );
    }
}