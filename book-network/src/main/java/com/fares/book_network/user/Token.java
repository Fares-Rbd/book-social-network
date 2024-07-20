package com.fares.book_network.user;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder // to use builder pattern
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to automatically generate the ID incrementally
    private Integer id;
    private String token;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime validatedAt;

    @ManyToOne //each user has many tokens and a token belongs to a single user
    @JoinColumn(nullable = false, name = "userId")
    private User user;

}
