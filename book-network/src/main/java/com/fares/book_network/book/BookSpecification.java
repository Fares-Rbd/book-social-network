package com.fares.book_network.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root //root is the entity we want to apply the specification on
                .get("owner").get("id"), ownerId);
    }
}
