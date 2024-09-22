package fr.codecake.spotifyclone.dto;

import fr.codecake.spotifyclone.vo.SongAuthorVO;
import fr.codecake.spotifyclone.vo.SongTitleVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SaveSongDTO(@Valid SongTitleVO title, @Valid SongAuthorVO author, @NotNull byte[] cover, @NotNull String coverContentType, @NotNull byte[] file, @NotNull String fileContentType) {
}
