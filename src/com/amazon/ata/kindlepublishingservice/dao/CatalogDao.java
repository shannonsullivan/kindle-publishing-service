package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     *
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null || book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }

        return book;
    }

    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
                .withHashKeyValues(book)
                .withScanIndexForward(false)
                .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    /**
     * Creates book if bookId is not provided and, Updates latest version of book
     * from catalog corresponding to the specified book id,
     * increments the version by 1 and sets previous version to inactive.
     * Throws a BookNotFoundException if bookId is invalid.
     *
     * @param formattedBook associated with bookId.
     * @return The new CatalogItem or an updated corresponding
     * CatalogItem from the catalog table.
     */
    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook formattedBook) {
        // If bookId not provided, CREATE new book
        if (formattedBook.getBookId() == null) {
            CatalogItemVersion item = new CatalogItemVersion();
            item.setBookId(KindlePublishingUtils.generateBookId());
            item.setVersion(1);
            item.setTitle(formattedBook.getTitle());
            item.setAuthor(formattedBook.getAuthor());
            item.setText(formattedBook.getText());
            item.setGenre(formattedBook.getGenre());
            dynamoDbMapper.save(item);
            return item;
        } else {
            // If invalid bookId is given
            if (getLatestVersionOfBook(formattedBook.getBookId()) == null) {
                //validateBookExists(formattedBook.getBookId());
                throw new BookNotFoundException(String.format("No book found for id: %s", formattedBook.getBookId()));
            }
            // If valid bookId is provided, then UPDATE version incremented by 1
            // and set previous version to inactive
            CatalogItemVersion versionUpdate = getLatestVersionOfBook(formattedBook.getBookId());
            removeBookFromCatalog(versionUpdate.getBookId());

            versionUpdate.setBookId(versionUpdate.getBookId());
            versionUpdate.setVersion(versionUpdate.getVersion() + 1);
            versionUpdate.setAuthor(versionUpdate.getAuthor());
            versionUpdate.setGenre(versionUpdate.getGenre());
            versionUpdate.setInactive(false);
            versionUpdate.setText(versionUpdate.getText());
            versionUpdate.setTitle(versionUpdate.getTitle());
            dynamoDbMapper.save(versionUpdate);

            return versionUpdate;
        }
    }

    /**
     * Sets the latest version of the book from the catalog corresponding to the specified book id as inactive.
     * Throws a BookNotFoundException if the latest version is not active or version is not found.
     *
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table set as inactive.
     */
    public CatalogItemVersion removeBookFromCatalog(String bookId) {
        CatalogItemVersion book = getBookFromCatalog(bookId);
        book.setInactive(true);
        dynamoDbMapper.save(book);

        return book;
    }

    /**
     * Checks if specified book id exists in catalog.
     * Throws a BookNotFoundException if book doesn't exist.
     *
     * @param bookId Id associated with the book.
     */
    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);

        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
    }
}
