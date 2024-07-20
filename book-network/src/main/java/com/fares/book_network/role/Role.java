package com.fares.book_network.role;


import com.fares.book_network.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.List;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder // to use builder pattern
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // to automatically populate createdAt and updatedAt fields
public class Role {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to automatically generate the ID incrementally
    private Integer id;

    @Column(unique = true) // to enforce uniqueness of the role
    private String name;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore // to avoid infinite recursion when serializing
    private List<User> users;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedDate;
}
