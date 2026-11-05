package com.example.pokeappv2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritosActivity extends AppCompatActivity {

    private LinearLayout containerFavorites;
    private TextView tvFavCount;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        containerFavorites = findViewById(R.id.containerFavorites);
        tvFavCount = findViewById(R.id.tvFavCount);
        sharedPreferences = getSharedPreferences("PokeFavorites", MODE_PRIVATE);

        renderFavorites();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderFavorites();
    }

    private void renderFavorites() {
        containerFavorites.removeAllViews();
        Set<String> favorites = sharedPreferences.getStringSet("fav_list", new HashSet<>());

        tvFavCount.setText(favorites.size() + " Guardados");

        if (favorites.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("¡Aún no tienes favoritos!\n\nVe a la Pokédex y toca la estrella ⭐.");
            emptyText.setTextColor(Color.parseColor("#94A3B8"));
            emptyText.setTextSize(16);
            emptyText.setGravity(Gravity.CENTER);
            emptyText.setPadding(0, 100, 0, 0);
            containerFavorites.addView(emptyText);
            return;
        }

        // Ordenar por ID del Pokémon
        List<String> sortedFavorites = new ArrayList<>(favorites);
        Collections.sort(sortedFavorites, (o1, o2) -> {
            try {
                int id1 = Integer.parseInt(o1.split(":")[0]);
                int id2 = Integer.parseInt(o2.split(":")[0]);
                return Integer.compare(id1, id2);
            } catch (Exception e) {
                return 0;
            }
        });

        for (String item : sortedFavorites) {
            String[] parts = item.split(":");
            if (parts.length == 2) {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                CardView card = createFavoriteCard(id, name, item);
                containerFavorites.addView(card);
            }
        }
    }

    private CardView createFavoriteCard(int id, String name, String rawItem) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);
        card.setRadius(32);
        card.setCardBackgroundColor(Color.parseColor("#1E293B"));
        card.setCardElevation(12);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(32, 24, 32, 24);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView ivSprite = new ImageView(this);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(140, 140);
        ivSprite.setLayoutParams(imgParams);

        String gifUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/showdown/" + id + ".gif";
        Glide.with(this).asGif().load(gifUrl).into(ivSprite);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textLayout.setLayoutParams(textParams);
        textLayout.setPadding(32, 0, 0, 0);

        TextView tvName = new TextView(this);
        tvName.setText(name.toUpperCase());
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(18);
        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvId = new TextView(this);
        tvId.setText(String.format("#%03d", id));
        tvId.setTextColor(Color.parseColor("#38BDF8"));
        tvId.setTextSize(13);

        textLayout.addView(tvName);
        textLayout.addView(tvId);

        ImageButton btnRemove = new ImageButton(this);
        btnRemove.setImageResource(android.R.drawable.btn_star_big_on);
        btnRemove.setBackgroundColor(Color.TRANSPARENT);
        btnRemove.setOnClickListener(v -> {
            Set<String> favorites = new HashSet<>(sharedPreferences.getStringSet("fav_list", new HashSet<>()));
            favorites.remove(rawItem);
            sharedPreferences.edit().putStringSet("fav_list", favorites).apply();
            Toast.makeText(this, "Eliminado de Favoritos ❌", Toast.LENGTH_SHORT).show();
            renderFavorites();
        });

        row.addView(ivSprite);
        row.addView(textLayout);
        row.addView(btnRemove);

        card.addView(row);
        return card;
    }
}