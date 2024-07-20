package com.fares.book_network.book;


import com.fares.book_network.file.FileUtils;
import com.fares.book_network.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookRequest bookRequest) {
        return Book
                .builder()
                .id(bookRequest.id())
                .title(bookRequest.title())
                .authorName(bookRequest.authorName())
                .isbn(bookRequest.isbn())
                .synopsis(bookRequest.synopsis())
                .shareable(bookRequest.shareable())
                .archived(false)
                .build();
    }

    public BookResponse toBookResponse(Book book) {
        return BookResponse
                .builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .shareable(book.isShareable())
                .archived(book.isArchived())
                .rating(book.getRating())
                .Owner(book.getOwner().getfullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder()
                .id(history.getBook().getId())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .isbn(history.getBook().getIsbn())
                .rating(history.getBook().getRating())
                .returned(history.isReturned())
                .returnApproved(history.isReturnApproved())
                .build();
    }
}
