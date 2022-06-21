package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.Book;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazon.ata.recommendationsservice.types.BookRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RemoveBookFromCatalogActivityTest {
    private static String BOOK_ID = "book.123";
    private static final String TITLE = "Title of Book";
    private static final String AUTHOR = "Book Author";
    private static final String ASIN = "B123456789";

    @Mock
    private CatalogDao catalogDao;

    @InjectMocks
    private RemoveBookFromCatalogActivity activity;

    @BeforeEach
    public void setup(){
        initMocks(this);
    }

    @Test
    public void execute_bookExists_returnsEmptyObject() {
        // GIVEN
        RemoveBookFromCatalogRequest request = RemoveBookFromCatalogRequest
                .builder()
                .withBookId(BOOK_ID)
                .build();

        CatalogItemVersion catalogItem = new CatalogItemVersion();
        catalogItem.setVersion(1);
        catalogItem.setBookId(BOOK_ID);
        catalogItem.setInactive(false);
        catalogItem.setGenre(BookGenre.FANTASY);

        List<BookRecommendation> bookRecommendations = new ArrayList<>();
        bookRecommendations.add(new BookRecommendation(TITLE, AUTHOR, ASIN));
        when(catalogDao.removeBookFromCatalog(BOOK_ID)).thenReturn(catalogItem);

        // WHEN
        RemoveBookFromCatalogResponse response = activity.execute(request);

        // THEN
        assertNotNull(response, "Expected request to return a non-null response.");
    }

    @Test
    public void execute_bookDoesNotExist_throwsException() {
        // GIVEN
        RemoveBookFromCatalogRequest request = RemoveBookFromCatalogRequest
                .builder()
                .withBookId("notAbook.123")
                .build();

        when(catalogDao.removeBookFromCatalog("notAbook.123")).thenThrow(new BookNotFoundException("No book found"));

        // WHEN & THEN
        assertThrows(BookNotFoundException.class, () -> activity.execute(request), "Expected activity to " +
                "throw an exception if the book can't be found.");
    }
}
