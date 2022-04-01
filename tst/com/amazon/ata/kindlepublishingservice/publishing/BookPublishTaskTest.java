package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazon.ata.recommendationsservice.types.BookGenre;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;


import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookPublishTaskTest {
    @Mock
    private DynamoDBMapper dynamoDbMapper;
    @Mock
    private BookPublishRequestManager publishRequestManager;
    @Mock
    private PublishingStatusDao publishingStatusDao;
    @Mock
    private CatalogDao catalogDao;
    @BeforeEach
    public void setup(){
        initMocks(this);
    }

    @Test
    public void run_completesSuccessfully() {
        // GIVEN
        //publishRequestManager = new BookPublishRequestManager();
        publishingStatusDao = new PublishingStatusDao(dynamoDbMapper);
        catalogDao = new CatalogDao(dynamoDbMapper);
        CatalogItemVersion catalogItem = new CatalogItemVersion();
        catalogItem.setVersion(1);
        catalogItem.setBookId("BookId");
        catalogItem.setInactive(false);
        catalogItem.setGenre(BookGenre.FANTASY);

        BookPublishRequest request = BookPublishRequest.builder()
                .withPublishingRecordId("RecordId")
                .withBookId("BookId")
                .withTitle("Title")
                .withAuthor("Author")
                .withText("Text")
                .withGenre(BookGenre.ACTION)
                .build();
        PublishingStatusItem item = new PublishingStatusItem();
        item.setPublishingRecordId("publishing.123");
        BookPublishTask task = new BookPublishTask(publishRequestManager, publishingStatusDao, catalogDao);
        String statusMessage = KindlePublishingUtils.generatePublishingStatusMessage(PublishingRecordStatus.IN_PROGRESS);

        //when(publishRequestManager.getBookPublishRequestToProcess()).thenReturn(request);
        when(publishingStatusDao.setPublishingStatus(anyString(),
              eq(PublishingRecordStatus.IN_PROGRESS),
               eq(request.getBookId()))).thenReturn(item);
        //when(catalogDao.getBookFromCatalog("BookID")).thenReturn(catalogItem);

        // WHEN && THEN
        task.run();
    }
}
