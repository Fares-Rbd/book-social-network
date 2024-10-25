package com.fares.book_network.feedback;


import com.fares.book_network.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
@Tag(name="Feedback", description="The Feedback API")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest feedbackRequest,
            Authentication connectedUser) {
        return ResponseEntity.ok(
                feedbackService.saveFeedback(feedbackRequest, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name="page",defaultValue = "0",required = false) Integer page,
            @RequestParam(name="size",defaultValue = "10",required = false) Integer size,
            Authentication connectedUser) {
        return ResponseEntity.ok(feedbackService.findAllFeedbackByBook(bookId, page, size, connectedUser));
    }

}
