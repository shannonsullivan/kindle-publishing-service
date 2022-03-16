package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.models.requests.RemoveBookFromCatalogRequest;
import com.amazon.ata.kindlepublishingservice.models.response.RemoveBookFromCatalogResponse;
import javax.inject.Inject;

/**
 * Implementation of the RemoveBookFromCatalogActivity for the ATACurriculumKindlePublishingService's
 * RemoveBookFromCatalog API.
 *
 * This API allows the client to remove a book (set the book to inactive).
 */

public class RemoveBookFromCatalogActivity {
    private CatalogDao catalogDao;

    /**
     * Instantiates a new RemoveBookFromCatalogActivity object.
     *
     * @param catalogDao CatalogDao to access the Catalog table.
     */
    @Inject
    public RemoveBookFromCatalogActivity(CatalogDao catalogDao) {
        this.catalogDao = catalogDao;
    }

    /**
     * Sets status as isInactive for book associated with the provided book id.
     *
     * @param removeBookFromCatalogRequest Request object containing the book ID associated
     *                                     with the book to remove.
     * @return RemoveBookFromCatalogResponse Response containing empty object.
     */

    public RemoveBookFromCatalogResponse execute(final RemoveBookFromCatalogRequest removeBookFromCatalogRequest) {
        catalogDao.removeBookFromCatalog(removeBookFromCatalogRequest.getBookId());
        return new RemoveBookFromCatalogResponse();
    }
}
