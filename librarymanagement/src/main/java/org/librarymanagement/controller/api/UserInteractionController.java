package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.entity.UserInteraction;
import org.librarymanagement.service.CurrentUserService;
import org.librarymanagement.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.USER_PUBLISHER)
public class UserInteractionController {

    private final FollowService followService;
    private final CurrentUserService currentUserService;

    public UserInteractionController(FollowService followService, CurrentUserService currentUserService) {
        this.followService = followService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/{slug}")
    public ResponseEntity<ResponseObject> followOrUnfollowPublisher(@PathVariable String slug) {

        User user = currentUserService.getCurrentUser();

        ResponseObject responseObject = followService.followOrUnfollowPublisher(user, slug);

        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }
}
