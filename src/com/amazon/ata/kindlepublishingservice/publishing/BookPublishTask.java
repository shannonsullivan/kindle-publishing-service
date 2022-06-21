package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * A Runnable that starts new thread to process a publish request from the BookPublishingManger.
 */
public class BookPublishTask implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);

    private BookPublishRequestManager publishRequestManager;
    private PublishingStatusDao publishingStatusDao;
    private CatalogDao catalogDao;

    /**
     * Constructs a BookPublishingTask object.
     *
     * @param publishRequest
     * @param publishingStatusDao
     * @param catalogDao
     */
    @Inject
    public BookPublishTask(BookPublishRequestManager publishRequest,
                           PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.publishRequestManager = publishRequest;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    /**
     * Retrieve a book publishing request, sets publishing status to
     * In_PROGRESS, converts request and either adds book to catalog
     * if valid and sets status to SUCCESSFUL or FAILED if invalid.
     */
    @Override
    public void run() {
        LOGGER.info(">>BookPublishTask Task run()");
        BookPublishRequest publishRequest = publishRequestManager.getBookPublishRequestToProcess();
        LOGGER.info("BookPublishRequest from QUEUE");

        if (publishRequest == null) {
            LOGGER.info("BookPublishingRequest QUEUE IS NULL");
            return;
        }

        publishingStatusDao.setPublishingStatus(
                publishRequest.getPublishingRecordId(),
                PublishingRecordStatus.IN_PROGRESS,
                publishRequest.getBookId());
        LOGGER.info(">>>>>>PublishingStatusDao IN_PROGRESS");

        try {
            KindleFormattedBook kindleFormattedBook = KindleFormatConverter.format(publishRequest);
            LOGGER.info(">>>>>>KINDLE FORMAT CONVERTER");

            CatalogItemVersion book = catalogDao.createOrUpdateBook(kindleFormattedBook);
            LOGGER.info("CALL TO UPDATE CATALOG DAO");
            publishingStatusDao.setPublishingStatus(
                    publishRequest.getPublishingRecordId(),
                    PublishingRecordStatus.SUCCESSFUL,
                    book.getBookId());
            LOGGER.info(">>>>>>>>>>>>>>>CatalogDao SUCCESSFUL");

        } catch (Exception e) {
            // If invalid bookId is given
            publishingStatusDao.setPublishingStatus(
                    publishRequest.getPublishingRecordId(),
                    PublishingRecordStatus.FAILED,
                    publishRequest.getBookId());
            LOGGER.info(">>>>>>>>>>>>>>>CatalogDao FAILED");
            e.getMessage();
        }
    }
}
