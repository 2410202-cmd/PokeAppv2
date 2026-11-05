package com.example.pokeappv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PokedexGridAdapter extends RecyclerView.Adapter<PokedexGridAdapter.ViewHolder> {

    public static class PokemonBasicItem {
        public int id;
        public String name;
        public String imageUrl;
        public boolean isFavorite = false;

        public PokemonBasicItem(int id, String name, String imageUrl) {
            this.id = id;
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }

    public interface OnPokemonClickListener {
        void onPokemonClick(PokemonBasicItem pokemon);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(PokemonBasicItem pokemon);
    }

    private Context context;
    private List<PokemonBasicItem> pokemonList;
    private OnPokemonClickListener listener;
    private OnFavoriteClickListener favoriteListener;

    public PokedexGridAdapter(Context context, List<PokemonBasicItem> pokemonList, OnPokemonClickListener listener) {
        this(context, pokemonList, listener, null);
    }

    public PokedexGridAdapter(Context context, List<PokemonBasicItem> pokemonList, OnPokemonClickListener listener, OnFavoriteClickListener favoriteListener) {
        this.context = context;
        this.pokemonList = pokemonList;
        this.listener = listener;
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pokemon_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PokemonBasicItem pokemon = pokemonList.get(position);
        holder.tvName.setText(pokemon.name.toUpperCase());
        holder.tvId.setText("#" + String.format("%04d", pokemon.id));
        holder.ivFav.setImageResource(pokemon.isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        Glide.with(context)
                .load(pokemon.imageUrl)
                .into(holder.ivSprite);

        holder.itemView.setOnClickListener(v -> listener.onPokemonClick(pokemon));

        if (holder.ivFav != null) {
            holder.ivFav.setOnClickListener(v -> {
                if (favoriteListener != null) {
                    favoriteListener.onFavoriteClick(pokemon);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId;
        ImageView ivSprite, ivFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGridName);
            tvId = itemView.findViewById(R.id.tvGridId);
            ivSprite = itemView.findViewById(R.id.ivGridSprite);
            ivFav = itemView.findViewById(R.id.ivGridFav);
        }
    }
}