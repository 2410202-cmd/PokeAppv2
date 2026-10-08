package com.example.pokeappv2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Random;

public class ZonaSafariActivity extends AppCompatActivity {
    private ImageView ivSafariPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona_safari);

        ivSafariPokemon = findViewById(R.id.ivSafariPokemon);
        Button btnCatch = findViewById(R.id.btnCatch);
        Button btnFlee = findViewById(R.id.btnFlee);

        nuevoPokemon();

        btnCatch.setOnClickListener(v -> {
            boolean atrapado = new Random().nextBoolean();
            if (atrapado) {
                Toast.makeText(this, "¡Atrapado con éxito!", Toast.LENGTH_SHORT).show();
                nuevoPokemon();
            } else {
                Toast.makeText(this, "¡Se escapó! Intenta de nuevo", Toast.LENGTH_SHORT).show();
            }
        });

        btnFlee.setOnClickListener(v -> nuevoPokemon());
    }

    private void nuevoPokemon() {
        int randomId = new Random().nextInt(150) + 1;
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/" + randomId + ".gif";
        Glide.with(this).asGif().load(url).into(ivSafariPokemon);
    }
}