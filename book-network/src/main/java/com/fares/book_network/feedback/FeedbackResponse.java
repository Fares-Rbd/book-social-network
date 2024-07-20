package com.fares.book_network.feedback;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Double rating;
    private String comment;
    private boolean ownFeedback; //feedback of the connected user (we will highlight it in a different color)
}
