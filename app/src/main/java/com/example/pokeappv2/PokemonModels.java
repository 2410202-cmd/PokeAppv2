package com.example.pokeappv2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PokemonModels {

    // Lista general de Pokémon
    public static class PokemonListResponse {
        @SerializedName("results")
        public List<PokemonNamedApiResource> results;
    }

    public static class PokemonNamedApiResource {
        @SerializedName("name")
        public String name;
        @SerializedName("url")
        public String url;
    }

    // Detalle de Pokémon
    public static class PokemonDetailResponse {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("height")
        public int height;
        @SerializedName("weight")
        public int weight;
        @SerializedName("sprites")
        public Sprites sprites;
        @SerializedName("types")
        public List<TypeSlot> types;
        @SerializedName("stats")
        public List<StatSlot> stats;
        @SerializedName("abilities")
        public List<AbilitySlot> abilities;
        @SerializedName("cries")
        public Cries cries;
    }

    public static class Sprites {
        @SerializedName("front_default")
        public String frontDefault;
        @SerializedName("other")
        public OtherSprites other;
    }

    public static class OtherSprites {
        @SerializedName("showdown")
        public ShowdownSprites showdown;
        @SerializedName("official-artwork")
        public OfficialArtwork officialArtwork;
    }

    public static class ShowdownSprites {
        @SerializedName("front_default")
        public String frontDefault;
    }

    public static class OfficialArtwork {
        @SerializedName("front_default")
        public String frontDefault;
    }

    public static class TypeSlot {
        @SerializedName("type")
        public PokemonNamedApiResource type;
    }

    public static class StatSlot {
        @SerializedName("base_stat")
        public int baseStat;
        @SerializedName("stat")
        public PokemonNamedApiResource stat;
    }

    public static class AbilitySlot {
        @SerializedName("ability")
        public PokemonNamedApiResource ability;
    }

    public static class Cries {
        @SerializedName("latest")
        public String latest;
        @SerializedName("legacy")
        public String legacy;
    }

    // Especies y Variedades
    public static class PokemonSpeciesResponse {
        @SerializedName("flavor_text_entries")
        public List<FlavorTextEntry> flavorTextEntries;
        @SerializedName("varieties")
        public List<VarietySlot> varieties;
    }

    public static class SpeciesResponse extends PokemonSpeciesResponse {}

    public static class VarietySlot {
        @SerializedName("is_default")
        public boolean isDefault;
        @SerializedName("pokemon")
        public PokemonNamedApiResource pokemon;
    }

    public static class FlavorTextEntry {
        @SerializedName("flavor_text")
        public String flavorText;
        @SerializedName("language")
        public PokemonNamedApiResource language;
    }

    // Movimientos / Ataques
    public static class MoveResponse {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("accuracy")
        public int accuracy;
        @SerializedName("power")
        public int power;
        @SerializedName("pp")
        public int pp;
        @SerializedName("priority")
        public int priority;
        @SerializedName("damage_class")
        public PokemonNamedApiResource damageClass;
        @SerializedName("type")
        public PokemonNamedApiResource type;
        @SerializedName("effect_entries")
        public List<MoveEffectEntry> effectEntries;
    }

    public static class MoveEffectEntry {
        @SerializedName("effect")
        public String effect;
        @SerializedName("language")
        public PokemonNamedApiResource language;
    }

    // Tipos (Efectividades de daño)
    public static class TypeResponse {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("damage_relations")
        public DamageRelations damageRelations;
    }

    public static class DamageRelations {
        @SerializedName("no_damage_to")
        public List<PokemonNamedApiResource> noDamageTo;
        @SerializedName("half_damage_to")
        public List<PokemonNamedApiResource> halfDamageTo;
        @SerializedName("double_damage_to")
        public List<PokemonNamedApiResource> doubleDamageTo;
        @SerializedName("no_damage_from")
        public List<PokemonNamedApiResource> noDamageFrom;
        @SerializedName("half_damage_from")
        public List<PokemonNamedApiResource> halfDamageFrom;
        @SerializedName("double_damage_from")
        public List<PokemonNamedApiResource> doubleDamageFrom;
    }
}