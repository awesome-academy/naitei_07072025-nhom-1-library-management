package org.librarymanagement.service.impl;

import org.librarymanagement.constant.InteractionConstant;
import org.librarymanagement.constant.TargetTypeConstant;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.entity.User;
import org.librarymanagement.entity.UserInteraction;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.PublisherRepository;
import org.librarymanagement.repository.UserInteractionRepository;
import org.librarymanagement.service.FollowService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.OptionalInt;

@Service
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final UserInteractionRepository userInteractionRepository;
    private final PublisherRepository publisherRepository;
    private final MessageSource messageSource;

    public FollowServiceImpl(UserInteractionRepository userInteractionRepository,
                             PublisherRepository publisherRepository,
                             MessageSource messageSource) {
        this.userInteractionRepository = userInteractionRepository;
        this.publisherRepository = publisherRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public ResponseObject followOrUnfollowPublisher(User user, String slug) {

        Publisher publisher = publisherRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà xuất bản với slug: " + slug));

        Optional<UserInteraction> optionaluUserInteraction = userInteractionRepository.findByUserIdAndTargetIdAndTargetTypeAndAction(
                user.getId(),
                publisher.getId(),
                TargetTypeConstant.PUBLISHER,
                InteractionConstant.FOLLOW
        );

        if(optionaluUserInteraction.isPresent()) {
            userInteractionRepository.deleteByUserIdAndTargetIdAndTargetTypeAndAction(
                    user.getId(),
                    publisher.getId(),
                    TargetTypeConstant.PUBLISHER,
                    InteractionConstant.FOLLOW
            );

            return new ResponseObject(
                    messageSource.getMessage(
                            "publisher.detail.unfollow",
                            null,
                            LocaleContextHolder.getLocale()
                    ),
                    200,
                    null
            );
        }

        UserInteraction userInteraction = new UserInteraction();
        userInteraction.setUser(user);
        userInteraction.setTargetId(publisher.getId());
        userInteraction.setTargetType(TargetTypeConstant.PUBLISHER);
        userInteraction.setAction(InteractionConstant.FOLLOW);
        userInteractionRepository.save(userInteraction);
        return new ResponseObject(
                messageSource.getMessage(
                        "publisher.detail.follow",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                200,
                null
        );
    }
}
