package com.fares.book_network.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends
        JpaRepository<Book, Integer>,
        JpaSpecificationExecutor<Book>  //we need this interface to use the Specification class
{

    @Query("""
        SELECT b FROM Book b
        WHERE b.owner.id != :userId
        AND b.archived = false
        AND b.shareable = true
        """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
}
