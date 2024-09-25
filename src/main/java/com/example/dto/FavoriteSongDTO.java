package com.example.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FavoriteSongDTO(@NotNull boolean favorite, @NotNull UUID publicId) {
}
