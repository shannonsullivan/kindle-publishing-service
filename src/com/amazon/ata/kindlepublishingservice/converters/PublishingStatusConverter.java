package com.amazon.ata.kindlepublishingservice.converters;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for PublishingStatusItem related objects.
 */
public class PublishingStatusConverter {

    private PublishingStatusConverter() {}

    /**
     * Converts the given PublishingStatusItem object to a PublishingStatusRecord.
     *
     * @param statusItem The PublishingStatusItem object to convert.
     * @return The converted PublishingStatusRecord.
     */
    public static PublishingStatusRecord toPublishingStatus(PublishingStatusItem statusItem) {

        return PublishingStatusRecord.builder()
                .withBookId(statusItem.getBookId())
                .withStatus(String.valueOf(statusItem.getStatus()))
                .withStatusMessage(statusItem.getStatusMessage())
                .build();
    }

    /**
     * Converts the given PublishingStatusItem list into the corresponding PublishingStatusRecord list.
     *
     * @param statusItem BookRecommendations list to convert.
     * @return The converted PublishingStatusRecord list.
     */
    public static List<PublishingStatusRecord> toPublishingStatusList(List<PublishingStatusItem> statusItem) {

        List<PublishingStatusRecord> publishingStatusList = new ArrayList<>();

        for (PublishingStatusItem item : statusItem) {
            publishingStatusList.add(toPublishingStatus(item));
        }

        return publishingStatusList;
    }
}
