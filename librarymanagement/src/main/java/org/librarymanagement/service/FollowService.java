package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.entity.UserInteraction;
import org.springframework.http.ResponseEntity;

public interface FollowService {
    public ResponseObject followOrUnfollowPublisher(User user, String slug);
}
