package com.example.pokeappv2;

import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PokedexActivity extends AppCompatActivity {

    private ImageButton btnBack, btnPrevForm, btnNextForm;
    private EditText etSearchPokemon;
    private Button btnFavoritos, btnPlaySound;
    private ImageView ivPokemonMain, ivStarFavorite;
    private TextView tvPokemonId, tvPokemonName, tvFormIndicator, tvPokemonType, tvHeightWeight, tvAbilities;
    private TextView tvHp, tvAttack, tvDefense, tvSpAtk, tvSpDef, tvSpeed, tvAlternateFormsTitle;
    private ProgressBar pbHp, pbAttack, pbDefense, pbSpAtk, pbSpDef, pbSpeed;
    private LinearLayout layoutFormsContainer;

    private PokemonModels.PokemonDetailResponse currentPokemon;
    private String soundUrl = "";
    private boolean isFavorite = false;
    private List<PokemonModels.VarietySlot> currentVarieties = new ArrayList<>();
    private int currentVarietyIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex); // VINCULADO A activity_pokedex.xml

        initViews();
        setupListeners();

        // Cargar por defecto Pikachu (#25)
        loadPokemonDetail("25");
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPrevForm = findViewById(R.id.btnPrevForm);
        btnNextForm = findViewById(R.id.btnNextForm);
        etSearchPokemon = findViewById(R.id.etSearchPokemon);
        btnFavoritos = findViewById(R.id.btnFavoritos);
        btnPlaySound = findViewById(R.id.btnPlaySound);

        ivPokemonMain = findViewById(R.id.ivPokemonMain);
        ivStarFavorite = findViewById(R.id.ivStarFavorite);

        tvPokemonId = findViewById(R.id.tvPokemonId);
        tvPokemonName = findViewById(R.id.tvPokemonName);
        tvFormIndicator = findViewById(R.id.tvFormIndicator);
        tvPokemonType = findViewById(R.id.tvPokemonType);
        tvHeightWeight = findViewById(R.id.tvHeightWeight);
        tvAbilities = findViewById(R.id.tvAbilities);

        tvHp = findViewById(R.id.tvHp);
        tvAttack = findViewById(R.id.tvAttack);
        tvDefense = findViewById(R.id.tvDefense);
        tvSpAtk = findViewById(R.id.tvSpAtk);
        tvSpDef = findViewById(R.id.tvSpDef);
        tvSpeed = findViewById(R.id.tvSpeed);

        pbHp = findViewById(R.id.pbHp);
        pbAttack = findViewById(R.id.pbAttack);
        pbDefense = findViewById(R.id.pbDefense);
        pbSpAtk = findViewById(R.id.pbSpAtk);
        pbSpDef = findViewById(R.id.pbSpDef);
        pbSpeed = findViewById(R.id.pbSpeed);

        tvAlternateFormsTitle = findViewById(R.id.tvAlternateFormsTitle);
        layoutFormsContainer = findViewById(R.id.layoutFormsContainer);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etSearchPokemon.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String query = etSearchPokemon.getText().toString().trim().toLowerCase();
                if (!query.isEmpty()) loadPokemonDetail(query);
                return true;
            }
            return false;
        });

        ivStarFavorite.setOnClickListener(v -> toggleFavorite());

        btnPlaySound.setOnClickListener(v -> playPokemonCry());

        btnPrevForm.setOnClickListener(v -> {
            if (!currentVarieties.isEmpty() && currentVarietyIndex > 0) {
                currentVarietyIndex--;
                loadPokemonDetail(currentVarieties.get(currentVarietyIndex).pokemon.name);
            }
        });

        btnNextForm.setOnClickListener(v -> {
            if (!currentVarieties.isEmpty() && currentVarietyIndex < currentVarieties.size() - 1) {
                currentVarietyIndex++;
                loadPokemonDetail(currentVarieties.get(currentVarietyIndex).pokemon.name);
            }
        });
    }

    private void loadPokemonDetail(String nameOrId) {
        ApiClient.getService().getPokemonDetail(nameOrId).enqueue(new Callback<PokemonModels.PokemonDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonModels.PokemonDetailResponse> call, @NonNull Response<PokemonModels.PokemonDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPokemon = response.body();
                    updateUI();
                    loadPokemonSpecies(String.valueOf(currentPokemon.id));
                } else {
                    Toast.makeText(PokedexActivity.this, "Pokémon no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonModels.PokemonDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(PokedexActivity.this, "Error de red al consultar Pokémon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentPokemon == null) return;

        tvPokemonId.setText(String.format("#%03d", currentPokemon.id));
        tvPokemonName.setText(currentPokemon.name.toUpperCase());

        // Dimensiones
        tvHeightWeight.setText("ALTURA: " + (currentPokemon.height / 10.0) + "m | PESO: " + (currentPokemon.weight / 10.0) + "kg");

        // Tipos
        StringBuilder types = new StringBuilder("TIPO: ");
        if (currentPokemon.types != null) {
            for (int i = 0; i < currentPokemon.types.size(); i++) {
                types.append(currentPokemon.types.get(i).type.name.toUpperCase());
                if (i < currentPokemon.types.size() - 1) types.append(" / ");
            }
        }
        tvPokemonType.setText(types.toString());

        // Habilidades
        StringBuilder abilities = new StringBuilder("HABILIDADES: ");
        if (currentPokemon.abilities != null) {
            for (int i = 0; i < currentPokemon.abilities.size(); i++) {
                abilities.append(currentPokemon.abilities.get(i).ability.name.toUpperCase());
                if (i < currentPokemon.abilities.size() - 1) abilities.append(", ");
            }
        }
        tvAbilities.setText(abilities.toString());

        // Estadísticas
        if (currentPokemon.stats != null) {
            for (PokemonModels.StatSlot slot : currentPokemon.stats) {
                switch (slot.stat.name.toLowerCase()) {
                    case "hp":
                        pbHp.setProgress(slot.baseStat);
                        tvHp.setText(String.valueOf(slot.baseStat));
                        break;
                    case "attack":
                        pbAttack.setProgress(slot.baseStat);
                        tvAttack.setText(String.valueOf(slot.baseStat));
                        break;
                    case "defense":
                        pbDefense.setProgress(slot.baseStat);
                        tvDefense.setText(String.valueOf(slot.baseStat));
                        break;
                    case "special-attack":
                        pbSpAtk.setProgress(slot.baseStat);
                        tvSpAtk.setText(String.valueOf(slot.baseStat));
                        break;
                    case "special-defense":
                        pbSpDef.setProgress(slot.baseStat);
                        tvSpDef.setText(String.valueOf(slot.baseStat));
                        break;
                    case "speed":
                        pbSpeed.setProgress(slot.baseStat);
                        tvSpeed.setText(String.valueOf(slot.baseStat));
                        break;
                }
            }
        }

        // Imagen
        String imageUrl = getPokemonImageUrl(currentPokemon);
        Glide.with(this).load(imageUrl).into(ivPokemonMain);

        // Sonido
        soundUrl = (currentPokemon.cries != null) ? currentPokemon.cries.latest : "";

        // Favorito
        checkFavoriteStatus();
    }

    private String getPokemonImageUrl(PokemonModels.PokemonDetailResponse pokemon) {
        if (pokemon.sprites != null && pokemon.sprites.other != null) {
            if (pokemon.sprites.other.officialArtwork != null && pokemon.sprites.other.officialArtwork.frontDefault != null) {
                return pokemon.sprites.other.officialArtwork.frontDefault;
            }
            if (pokemon.sprites.other.showdown != null && pokemon.sprites.other.showdown.frontDefault != null) {
                return pokemon.sprites.other.showdown.frontDefault;
            }
        }
        return (pokemon.sprites != null) ? pokemon.sprites.frontDefault : "";
    }

    private void loadPokemonSpecies(String speciesId) {
        ApiClient.getService().getPokemonSpecies(speciesId).enqueue(new Callback<PokemonModels.PokemonSpeciesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonModels.PokemonSpeciesResponse> call, @NonNull Response<PokemonModels.PokemonSpeciesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentVarieties = response.body().varieties;

                    if (currentVarieties != null && currentVarieties.size() > 1) {
                        tvAlternateFormsTitle.setVisibility(View.VISIBLE);
                        layoutFormsContainer.removeAllViews();

                        for (int i = 0; i < currentVarieties.size(); i++) {
                            PokemonModels.VarietySlot slot = currentVarieties.get(i);
                            if (slot.pokemon.name.equalsIgnoreCase(currentPokemon.name)) {
                                currentVarietyIndex = i;
                            }
                            addFormCard(slot.pokemon.name);
                        }

                        btnPrevForm.setVisibility(currentVarietyIndex > 0 ? View.VISIBLE : View.GONE);
                        btnNextForm.setVisibility(currentVarietyIndex < currentVarieties.size() - 1 ? View.VISIBLE : View.GONE);
                        tvFormIndicator.setVisibility(View.VISIBLE);
                        tvFormIndicator.setText(currentPokemon.name.replace("-", " ").toUpperCase());
                    } else {
                        tvAlternateFormsTitle.setVisibility(View.GONE);
                        btnPrevForm.setVisibility(View.GONE);
                        btnNextForm.setVisibility(View.GONE);
                        tvFormIndicator.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonModels.PokemonSpeciesResponse> call, @NonNull Throwable t) {
                tvAlternateFormsTitle.setVisibility(View.GONE);
            }
        });
    }

    private void addFormCard(String pokemonName) {
        ApiClient.getService().getPokemonDetail(pokemonName).enqueue(new Callback<PokemonModels.PokemonDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonModels.PokemonDetailResponse> call, @NonNull Response<PokemonModels.PokemonDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonModels.PokemonDetailResponse detail = response.body();

                    ImageView cardImageView = new ImageView(PokedexActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 140);
                    params.setMargins(12, 8, 12, 8);
                    cardImageView.setLayoutParams(params);
                    cardImageView.setPadding(12, 12, 12, 12);

                    cardImageView.setBackgroundColor(0xFF1E293B);

                    String spriteUrl = (detail.sprites != null) ? detail.sprites.frontDefault : "";
                    Glide.with(PokedexActivity.this).load(spriteUrl).into(cardImageView);

                    cardImageView.setOnClickListener(v -> loadPokemonDetail(detail.name));
                    layoutFormsContainer.addView(cardImageView);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonModels.PokemonDetailResponse> call, @NonNull Throwable t) {}
        });
    }

    private void playPokemonCry() {
        if (soundUrl == null || soundUrl.isEmpty()) {
            Toast.makeText(this, "Sonido no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(soundUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        } catch (IOException e) {
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkFavoriteStatus() {
        SharedPreferences prefs = getSharedPreferences("PokemonFavs", MODE_PRIVATE);
        Set<String> favs = prefs.getStringSet("fav_list", new HashSet<>());
        isFavorite = favs.contains(String.valueOf(currentPokemon.id));
        ivStarFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }

    private void toggleFavorite() {
        if (currentPokemon == null) return;

        SharedPreferences prefs = getSharedPreferences("PokemonFavs", MODE_PRIVATE);
        Set<String> favs = new HashSet<>(prefs.getStringSet("fav_list", new HashSet<>()));

        String idStr = String.valueOf(currentPokemon.id);
        if (isFavorite) {
            favs.remove(idStr);
            isFavorite = false;
            Toast.makeText(this, "Eliminado de Favoritos", Toast.LENGTH_SHORT).show();
        } else {
            favs.add(idStr);
            isFavorite = true;
            Toast.makeText(this, "Guardado en Favoritos", Toast.LENGTH_SHORT).show();
        }

        prefs.edit().putStringSet("fav_list", favs).apply();
        ivStarFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
    }
}