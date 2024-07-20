package com.fares.book_network.user;


import com.fares.book_network.book.Book;
import com.fares.book_network.history.BookTransactionHistory;
import com.fares.book_network.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder // to use builder pattern
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user") // user is a reserved keyword in SQL
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // to automatically generate the ID incrementally
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    @Column(unique = true) // to enforce uniqueness
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER) // to eagerly fetch the roles when fetching a user
    private List<Role> roles;

    @OneToMany(mappedBy = "owner") //one user owns many books
    private List<Book> books;


    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory> histories;

    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles
                .stream() // to convert the list of roles to a stream
                .map(role -> new SimpleGrantedAuthority(role.getName())) // to convert each role to a GrantedAuthority
                .collect(Collectors.toList()); // to convert the list of roles to a list of GrantedAuthority
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getfullName(){
        return this.firstname + " " + this.lastname;
    }
}
