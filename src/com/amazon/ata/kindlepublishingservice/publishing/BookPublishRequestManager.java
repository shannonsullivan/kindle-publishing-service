package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class responsible for managing a collection of book publishing requests allowing
 * the request to be processed in the same order they are submitted.
 */
public class BookPublishRequestManager {
    private Queue<BookPublishRequest> requestQueue;

    @Inject
    public BookPublishRequestManager(ConcurrentLinkedQueue<BookPublishRequest> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        requestQueue.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        return requestQueue.poll();
    }
}
