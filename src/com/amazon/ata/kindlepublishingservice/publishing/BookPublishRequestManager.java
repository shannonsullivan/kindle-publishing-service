package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class responsible for saving each book publish request allowing the
 * request to be processed in the same order they are submitted.
 */
public class BookPublishRequestManager {
    private Queue<BookPublishRequest> requestQueue;

    @Inject
    public BookPublishRequestManager() {
        requestQueue = new LinkedList<>();
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        requestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        if (requestQueue.isEmpty()) {
            return null;
        }
        return requestQueue.peek();
    }
}
