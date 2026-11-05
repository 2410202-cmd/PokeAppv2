package com.example.pokeappv2;

import com.google.gson.annotations.SerializedName;

public class PokemonResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("sprites")
    private PokemonModels.Sprites sprites;

    public int getId() { return id; }
    public String getName() { return name; }
    public PokemonModels.Sprites getSprites() { return sprites; }
}