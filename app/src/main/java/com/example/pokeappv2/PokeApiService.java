package com.example.pokeappv2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeApiService {

    // 1. Obtener Pokémon por ID o Nombre (Modelo Simple / Completo)
    @GET("pokemon/{idOrName}")
    Call<PokemonResponse> getPokemon(@Path("idOrName") String idOrName);

    // 2. Obtener Lista de Pokémon para Pokédex / Menús de Selección
    @GET("pokemon")
    Call<PokemonModels.PokemonListResponse> getPokemonList(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    // 3. Obtener Especie de Pokémon (Cadenas evolutivas, descripciones)
    @GET("pokemon-species/{idOrName}")
    Call<PokemonModels.PokemonSpeciesResponse> getPokemonSpecies(
            @Path("idOrName") String idOrName
    );

    // 4. Detalle Completo de Pokémon
    @GET("pokemon/{idOrName}")
    Call<PokemonModels.PokemonDetailResponse> getPokemonDetail(
            @Path("idOrName") String idOrName
    );

    // 5. Obtener información de Movimientos / Ataques
    @GET("move/{idOrName}")
    Call<PokemonModels.MoveResponse> getMoveDetail(
            @Path("idOrName") String idOrName
    );

    // 6. Obtener información de Tipos (Efectividades de daño)
    @GET("type/{idOrName}")
    Call<PokemonModels.TypeResponse> getTypeDetail(
            @Path("idOrName") String idOrName
    );
}