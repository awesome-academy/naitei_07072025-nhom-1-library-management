package org.librarymanagement.dto.response;

import java.util.List;

public record PublisherInfoResponse(
        String name,
        String address,
        String mail,
        List<PublisherBookResponse> books
) {}
