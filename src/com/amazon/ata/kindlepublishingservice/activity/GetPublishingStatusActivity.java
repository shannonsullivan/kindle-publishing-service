package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.converters.PublishingStatusConverter;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of the GetPublishingStatusActivity for ATACurriculumKindlePublishingService's
 * GetPublishingStatus API.
 *
 * This API allows the client to check on the state of their publishing request.
 */
public class GetPublishingStatusActivity {
    private PublishingStatusDao publishingStatusDao;

    /**
     * Instantiates a new GetPublishingStatusActivity object.
     *
     * @param publishingStatusDao PublishingStatusDao to access the publishing status table.
     */
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    /**
     * Submits the publishing record in the request for publishing status.
     *
     * @param publishingStatusRequest Request object containing the book status to be published.
     * @throws PublishingStatusNotFoundException if request id doesn't exist.
     *
     * @return GetPublishingStatusResponse Response object that includes the publishing status
     *          corresponding to the provided publishingStatusId.
     */
    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest)
            throws PublishingStatusNotFoundException {

        List<PublishingStatusItem> statusItemList = publishingStatusDao.getPublishingStatus(
                publishingStatusRequest.getPublishingRecordId());

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(PublishingStatusConverter.toPublishingStatusList(statusItemList))
                .build();
    }
}
