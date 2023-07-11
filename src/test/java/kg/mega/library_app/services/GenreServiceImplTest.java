package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.DuplicateException;
import kg.mega.library_app.dao.GenreRepo;
import kg.mega.library_app.models.dto.requests.GenreReq;
import kg.mega.library_app.models.entities.Genre;
import kg.mega.library_app.services.GenreService;
import kg.mega.library_app.services.impl.GenreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.util.PSQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {
    @Mock
    private GenreRepo genreRepo;

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        genreService = new GenreServiceImpl(genreRepo);
    }

    @Test
    void testGetByTitle_ExistingTitle_ShouldReturnGenre() {
        String title = "Fantasy";
        Genre expectedGenre = new Genre();
        expectedGenre.setTitle(title);
        when(genreRepo.findByTitle(title)).thenReturn(expectedGenre);

        Genre result = genreService.getByTitle(title);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        verify(genreRepo).findByTitle(title);
    }

    @Test
    void testGetByTitle_NonExistingTitle_ShouldReturnNull() {
        String title = "NonExistingGenre";
        when(genreRepo.findByTitle(title)).thenReturn(null);

        Genre result = genreService.getByTitle(title);

        assertNull(result);
        verify(genreRepo).findByTitle(title);
    }

    @Test
    void testCreateGenre_NewGenre_ShouldReturnCreatedGenre() throws DuplicateException {
        String title = "Fantasy";
        String description = "Fantasy Genre";
        GenreReq genreReq = new GenreReq(title, description);
        when(genreRepo.findByTitle(title)).thenReturn(null);
        when(genreRepo.save(any(Genre.class))).thenAnswer(invocation -> {
            Genre savedGenre = invocation.getArgument(0);
            savedGenre.setId(1L);
            return savedGenre;
        });

        Genre result = genreService.createGenre(genreReq);

        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(description, result.getDescription());
        verify(genreRepo).findByTitle(title);
        verify(genreRepo).save(any(Genre.class));
    }

    @Test
    void testCreateGenre_ExistingGenre_ShouldThrowDuplicateException() {
        String title = "Fantasy";
        String description = "Fantasy Genre";
        GenreReq genreReq = new GenreReq(title, description);
        when(genreRepo.findByTitle(title)).thenReturn(new Genre());

        assertThrows(DuplicateException.class, () -> genreService.createGenre(genreReq));
        verify(genreRepo).findByTitle(title);
        verify(genreRepo, never()).save(any(Genre.class));
    }

    @Test
    void testUpdateGenre_ExistingGenre_ShouldReturnSuccessMessage() throws DataNotFoundException {
        Long id = 1L;
        String title = "Fantasy";
        String description = "Fantasy Genre";
        GenreReq genreReq = new GenreReq(title, description);
        Genre existingGenre = new Genre();
        existingGenre.setId(id);
        when(genreRepo.findById(id)).thenReturn(Optional.of(existingGenre));
        when(genreRepo.save(any(Genre.class))).thenReturn(existingGenre);

        String result = genreService.updateGenre(id, genreReq);

        assertNotNull(result);
        assertTrue(result.contains("updated successfully"));
        verify(genreRepo).findById(id);
        verify(genreRepo).save(any(Genre.class));
    }

    @Test
    void testUpdateGenre_NonExistingGenre_ShouldThrowDataNotFoundException() {
        Long id = 1L;
        String title = "Fantasy";
        String description = "Fantasy Genre";
        GenreReq genreReq = new GenreReq(title, description);
        when(genreRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> genreService.updateGenre(id, genreReq));
        verify(genreRepo).findById(id);
        verify(genreRepo, never()).save(any(Genre.class));
    }

    @Test
    void testDeleteGenre_ExistingGenre_ShouldReturnSuccessMessage() throws DataNotFoundException, PSQLException {
        Long id = 1L;
        Genre existingGenre = new Genre();
        existingGenre.setId(id);
        when(genreRepo.findById(id)).thenReturn(Optional.of(existingGenre));

        String result = genreService.deleteGenre(id);

        assertNotNull(result);
        assertTrue(result.contains("deleted successfully"));
        verify(genreRepo).findById(id);
        verify(genreRepo).delete(existingGenre);
    }

    @Test
    void testDeleteGenre_NonExistingGenre_ShouldThrowDataNotFoundException() {
        Long id = 1L;
        when(genreRepo.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> genreService.deleteGenre(id));
        verify(genreRepo).findById(id);
        verify(genreRepo, never()).delete(any(Genre.class));
    }

    @Test
    void testGetAllByTitle_TitleContaining_ShouldReturnMatchingGenres() {
        String title = "Fan";
        List<Genre> expectedGenres = new ArrayList<>();
        expectedGenres.add(new Genre("Fantasy", "Fantasy Genre"));
        expectedGenres.add(new Genre("Fanfiction", "Fanfiction Genre"));
        when(genreRepo.findAllByTitleContaining(title)).thenReturn(expectedGenres);

        List<Genre> result = genreService.getAllByTitle(title);

        assertNotNull(result);
        assertEquals(expectedGenres.size(), result.size());
        for (int i = 0; i < expectedGenres.size(); i++) {
            Genre expectedGenre = expectedGenres.get(i);
            Genre actualGenre = result.get(i);
            assertEquals(expectedGenre.getTitle(), actualGenre.getTitle());
            assertEquals(expectedGenre.getDescription(), actualGenre.getDescription());
        }
        verify(genreRepo).findAllByTitleContaining(title);
    }
}