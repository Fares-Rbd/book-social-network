package com.fares.book_network.feedback;


import com.fares.book_network.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest feedbackRequest) {
        return  Feedback.builder()
                .rating(feedbackRequest.rating())
                .comment(feedbackRequest.comment())
                .book(Book.builder()
                        .id(feedbackRequest.bookId())
                        .archived(false) //Not Required and has no impact, just to satisfy lombok
                        .shareable(true)//Not Required and has no impact, just to satisfy lombok
                        .build())
                .build();

    }

    public FeedbackResponse toFeedBackResponse(Feedback feedback, Integer id) {

        return FeedbackResponse.builder()
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .ownFeedback(feedback.getCreatedBy().equals(id))
                .build();

    }

}
