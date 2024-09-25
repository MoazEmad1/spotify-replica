package com.example.dto;

import com.example.vo.SongAuthorVO;
import com.example.vo.SongTitleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SaveSongDTO(@Valid SongTitleVO title, @Valid SongAuthorVO author, @NotNull byte[] cover, @NotNull String coverContentType, @NotNull byte[] file, @NotNull String fileContentType) {
}
