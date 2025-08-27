package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.service.PublisherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiEndpoints.USER_PUBLISHER)
public class PublisherInfoController {

    private final PublisherService publisherService;

    public PublisherInfoController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseObject> getPublisherInfo(@PathVariable String slug) {

        ResponseObject responseObject = publisherService.getPublisher(slug);

        return ResponseEntity.status(responseObject.status())
                .body(responseObject);

    }
}
