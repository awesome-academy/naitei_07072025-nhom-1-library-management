package org.librarymanagement.service;

import org.librarymanagement.dto.response.PublisherInfoResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Publisher;

public interface PublisherService {
    Publisher findOrCreatePublisher(String publisherName);
    ResponseObject getPublisher(String slug);
}
