package org.librarymanagement.service.impl;

import org.librarymanagement.dto.response.*;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.Genre;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.repository.AuthorRepository;
import org.librarymanagement.repository.PublisherRepository;
import org.librarymanagement.service.PublisherService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PublisherServiceImpl implements PublisherService {

    private final PublisherRepository publisherRepository;
    private final MessageSource messageSource;

    public PublisherServiceImpl(PublisherRepository publisherRepository, MessageSource messageSource) {
        this.publisherRepository = publisherRepository;
        this.messageSource = messageSource;
    }

    public Publisher findOrCreatePublisher(String publisherName){
        return publisherRepository.findByName(publisherName)
                .orElseGet(() -> {
                    Publisher p = new Publisher();
                    p.setName(publisherName);
                    return publisherRepository.save(p);
                });
    }

    @Override
    public ResponseObject getPublisher(String slug){

        Publisher publisher = publisherRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhà xuất bản với slug: " + slug));

        PublisherInfoResponse publisherInfoResponse = new PublisherInfoResponse(
                publisher.getName(),
                publisher.getAddress(),
                publisher.getEmail(),
                convertToPublisherBookResponse(publisher.getBooks())
        );

        return new ResponseObject(
                messageSource.getMessage(
                        "publisher.detail",
                        null,
                        LocaleContextHolder.getLocale()
                ),
                200,
                publisherInfoResponse
        );

    }

    private List<PublisherBookResponse> convertToPublisherBookResponse(Set<Book> books) {
        if(books == null || books.isEmpty()){
            return new ArrayList<>();
        }

        List<PublisherBookResponse> publisherBookResponse = new ArrayList<>();

        publisherBookResponse = books.stream()
                .map(book -> {
                    return new PublisherBookResponse(
                            book.getTitle(),
                            book.getPublishedDay()
                    );
                })
                .toList();

        return publisherBookResponse;
    }
}
