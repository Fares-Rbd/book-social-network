package com.fares.book_network.book;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String Owner; //owner's full name
    private byte[] cover;
    private double rating;
    private boolean shareable;
    private boolean archived;

}
