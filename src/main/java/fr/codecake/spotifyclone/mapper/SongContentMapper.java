package fr.codecake.spotifyclone.mapper;

import fr.codecake.spotifyclone.domain.SongContent;
import fr.codecake.spotifyclone.dto.SaveSongDTO;
import fr.codecake.spotifyclone.dto.SongContentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SongContentMapper {

    @Mapping(source = "song.publicId", target = "publicId")
    SongContentDTO songContentToSongContentDTO(SongContent songContent);

    SongContent saveSongDTOToSong(SaveSongDTO songDTO);
}
