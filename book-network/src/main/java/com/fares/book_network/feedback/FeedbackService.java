package com.fares.book_network.feedback;

import com.fares.book_network.book.Book;
import com.fares.book_network.book.BookRepository;
import com.fares.book_network.common.PageResponse;
import com.fares.book_network.exception.OperationNotPermittedException;
import com.fares.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Integer saveFeedback(FeedbackRequest feedbackRequest, Authentication connectedUser) {

        //checking if the book exists in our database
        Book book = bookRepository.findById(feedbackRequest.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + feedbackRequest.bookId()));

        //checking if the book is not archived and it is shareable
        if(book.isArchived() || !book.isShareable())
            throw new OperationNotPermittedException("You are not allowed to give a feedback about this book (It is either archived or not shareable)");

        //checking weather the current user is the owner of the book
        User user = (User) connectedUser.getPrincipal();
        if(book.getOwner().getId().equals(user.getId()))
            throw new OperationNotPermittedException("You are not allowed to give a feedback about your own book");

        Feedback feedback = feedbackMapper.toFeedback(feedbackRequest);

        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, Integer page, Integer size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks= feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedBackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast());
    }
}
