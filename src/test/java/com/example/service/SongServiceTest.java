package com.example.service;

import com.example.domain.Favorite;
import com.example.domain.FavoriteId;
import com.example.domain.Song;
import com.example.domain.SongContent;
import com.example.dto.*;
import com.example.mapper.SongContentMapper;
import com.example.mapper.SongMapper;
import com.example.repository.SongRepository;
import com.example.vo.SongAuthorVO;
import com.example.vo.SongTitleVO;
import com.example.dto.*;
import com.example.repository.FavoriteRepository;
import com.example.repository.SongContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SongServiceTest {

    @Mock
    private SongMapper songMapper;

    @Mock
    private SongRepository songRepository;

    @Mock
    private SongContentRepository songContentRepository;

    @Mock
    private SongContentMapper songContentMapper;

    @Mock
    private UserService userService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private SongService songService;

    private Song song;
    private SongContent songContent;
    private ReadSongInfoDTO readSongInfoDTO;
    private SaveSongDTO saveSongDTO;
    private FavoriteSongDTO favoriteSongDTO;
    private ReadUserDTO readUserDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        song = new Song();
        song.setPublicId(UUID.randomUUID());

        songContent = new SongContent();
        readSongInfoDTO = new ReadSongInfoDTO();

        saveSongDTO = new SaveSongDTO(
                new SongTitleVO("Test Song Title"),
                new SongAuthorVO("Test Author"),
                new byte[]{1, 2, 3},
                "image/jpeg",
                new byte[]{4, 5, 6},
                "audio/mpeg"
        );

        favoriteSongDTO = new FavoriteSongDTO(true, song.getPublicId());
        readUserDTO = new ReadUserDTO("John", "Doe", "john.doe@example.com", "https://example.com/image.jpg");
    }


    @Test
    void testCreateSong() {
        when(songMapper.saveSongDTOToSong(saveSongDTO)).thenReturn(song);
        when(songRepository.save(any(Song.class))).thenReturn(song);
        when(songContentMapper.saveSongDTOToSong(saveSongDTO)).thenReturn(songContent);
        when(songMapper.songToReadSongInfoDTO(song)).thenReturn(readSongInfoDTO);

        ReadSongInfoDTO result = songService.create(saveSongDTO);

        verify(songRepository, times(1)).save(song);
        verify(songContentRepository, times(1)).save(songContent);

        assertNotNull(result);
    }

    @Test
    void testGetAllSongs_WhenAuthenticated() {
        when(songRepository.findAll()).thenReturn(List.of(song));
        when(songMapper.songToReadSongInfoDTO(song)).thenReturn(readSongInfoDTO);
        when(userService.isAuthenticated()).thenReturn(true);
        when(userService.getAuthenticatedUserFromSecurityContext()).thenReturn(readUserDTO);
        when(favoriteRepository.findAllByUserEmailAndSongPublicIdIn(anyString(), anyList())).thenReturn(List.of());

        List<ReadSongInfoDTO> result = songService.getAll();

        verify(songRepository, times(1)).findAll();
        verify(songMapper, times(1)).songToReadSongInfoDTO(song);

        assertEquals(1, result.size());
    }

    @Test
    void testGetOneByPublicId() {
        UUID publicId = UUID.randomUUID();
        byte[] file = new byte[]{1, 2, 3};
        String fileContentType = "audio/mpeg";

        when(songContentRepository.findOneBySongPublicId(publicId)).thenReturn(Optional.of(songContent));

        when(songContentMapper.songContentToSongContentDTO(songContent)).thenReturn(new SongContentDTO(publicId, file, fileContentType));

        Optional<SongContentDTO> result = songService.getOneByPublicId(publicId);

        verify(songContentRepository, times(1)).findOneBySongPublicId(publicId);
        verify(songContentMapper, times(1)).songContentToSongContentDTO(songContent);

        assertTrue(result.isPresent());
        assertEquals(publicId, result.get().publicId());
        assertArrayEquals(file, result.get().file());
        assertEquals(fileContentType, result.get().fileContentType());
    }



    @Test
    void testSearchSongs() {
        String searchTerm = "test";
        when(songRepository.findByTitleOrAuthorContaining(searchTerm)).thenReturn(List.of(song));
        when(songMapper.songToReadSongInfoDTO(song)).thenReturn(readSongInfoDTO);
        when(userService.isAuthenticated()).thenReturn(false);

        List<ReadSongInfoDTO> result = songService.search(searchTerm);

        verify(songRepository, times(1)).findByTitleOrAuthorContaining(searchTerm);

        assertEquals(1, result.size());
    }

    @Test
    void testAddToFavorites() {
        when(songRepository.findOneByPublicId(favoriteSongDTO.publicId())).thenReturn(Optional.of(song));
        when(userService.getByEmail(anyString())).thenReturn(Optional.of(readUserDTO));

        State<FavoriteSongDTO, String> result = songService.addOrRemoveFromFavorite(favoriteSongDTO, readUserDTO.email());

        verify(favoriteRepository, times(1)).save(any(Favorite.class));

        assertNotNull(result);
        assertEquals(StatusNotification.OK, result.getStatus());
    }

    @Test
    void testRemoveFromFavorites() {
        favoriteSongDTO = new FavoriteSongDTO(false, song.getPublicId());

        when(songRepository.findOneByPublicId(favoriteSongDTO.publicId())).thenReturn(Optional.of(song));
        when(userService.getByEmail(anyString())).thenReturn(Optional.of(readUserDTO));

        State<FavoriteSongDTO, String> result = songService.addOrRemoveFromFavorite(favoriteSongDTO, readUserDTO.email());

        verify(favoriteRepository, times(1)).deleteById(any(FavoriteId.class));

        assertNotNull(result);
        assertEquals(StatusNotification.OK, result.getStatus());
        assertFalse(result.getValue().favorite());
    }


    @Test
    void testFetchFavoriteSongs() {
        when(songRepository.findAllFavoriteByUserEmail(anyString())).thenReturn(List.of(song));
        when(songMapper.songToReadSongInfoDTO(song)).thenReturn(readSongInfoDTO);

        List<ReadSongInfoDTO> result = songService.fetchFavoriteSongs("test@example.com");

        verify(songRepository, times(1)).findAllFavoriteByUserEmail(anyString());
        verify(songMapper, times(1)).songToReadSongInfoDTO(song);

        assertEquals(1, result.size());
    }
}
