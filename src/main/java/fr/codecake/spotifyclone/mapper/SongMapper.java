package fr.codecake.spotifyclone.mapper;
import fr.codecake.spotifyclone.domain.Song;
import fr.codecake.spotifyclone.dto.ReadSongInfoDTO;
import fr.codecake.spotifyclone.dto.SaveSongDTO;
import fr.codecake.spotifyclone.vo.SongAuthorVO;
import fr.codecake.spotifyclone.vo.SongTitleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SongMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    Song saveSongDTOToSong(SaveSongDTO saveSongDTO);

    @Mapping(target = "favorite", ignore = true)
    ReadSongInfoDTO songToReadSongInfoDTO(Song song);

    default SongTitleVO stringToSongTitleVO(String title){
        return new SongTitleVO(title);
    }

    default SongAuthorVO stringToSongAuthorVO(String author){
        return new SongAuthorVO(author);
    }

    default String songTitleVOToString(SongTitleVO title) {
        return title.value();
    }

    default String songAuthorVOToString(SongAuthorVO author) {
        return author.value();
    }


}
