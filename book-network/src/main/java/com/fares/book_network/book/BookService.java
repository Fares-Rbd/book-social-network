package com.fares.book_network.book;

import com.fares.book_network.common.PageResponse;
import com.fares.book_network.exception.OperationNotPermittedException;
import com.fares.book_network.file.FileStorageService;
import com.fares.book_network.history.BookTransactionHistory;
import com.fares.book_network.history.BookTransactionHistoryRepository;
import com.fares.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.fares.book_network.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository historyRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal(); //since our user implements the Principal interface we can fetch it from the Authentication object
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        bookRepository.save(book);
        return book.getId();
    }

    public BookResponse findById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + id));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId()); //we are passing the user as a parameter because we want to display only the books that are not owned by the user
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrewedBooks = historyRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrewedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(bookResponse,
                allBorrewedBooks.getNumber(),
                allBorrewedBooks.getSize(),
                allBorrewedBooks.getTotalElements(),
                allBorrewedBooks.getTotalPages(),
                allBorrewedBooks.isFirst(),
                allBorrewedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = historyRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allReturnedBooks.stream().map(bookMapper::toBorrowedBookResponse).toList();
        return new PageResponse<>(bookResponse,
                allReturnedBooks.getNumber(),
                allReturnedBooks.getSize(),
                allReturnedBooks.getTotalElements(),
                allReturnedBooks.getTotalPages(),
                allReturnedBooks.isFirst(),
                allReturnedBooks.isLast());

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not allowed to update the shareable status of this book");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You are not allowed archive other users' books");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();
    }


    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        //checking if the book exists in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));

        //checking if the book is not archived and it is shareable
        if (book.isArchived() || !book.isShareable())
            throw new OperationNotPermittedException("You are not allowed to borrow this book (It is either archived or not shareable)");

        //checking weather the current user is the owner of the book
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId()))
            throw new OperationNotPermittedException("You are not allowed to borrow your own book");

        //checking if the book is already borrowed
        final boolean isAlreadyBorrowed = historyRepository.isCurrentlyBorrowed(bookId);
        if (isAlreadyBorrowed)
            throw new OperationNotPermittedException("You are not allowed to borrow a book that is already borrowed");

        //if all the previous checks passed we can create a new transaction history to apporve the borrowing operation
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        //checking if the book exists in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));

        //checking if the book is not archived and it is shareable
        if (book.isArchived() || !book.isShareable())
            throw new OperationNotPermittedException("You are not allowed to return this book (It is either archived or not shareable)");

        //checking weather the current user is the owner of the book
        User user = (User) connectedUser.getPrincipal();
        if (book.getOwner().getId().equals(user.getId()))
            throw new OperationNotPermittedException("You are not allowed to return your own book");

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    public Integer approveReturnedBook(Integer bookId, Authentication connectedUser) {
        //checking if the book exists in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));

        //checking if the book is not archived and it is shareable
        if (book.isArchived() || !book.isShareable())
            throw new OperationNotPermittedException("You are not allowed to approve return this book (It is either archived or not shareable)");

        //checking weather the current user is the owner of the book
        User user = (User) connectedUser.getPrincipal();
        if (!book.getOwner().getId().equals(user.getId()))
            throw new OperationNotPermittedException("You are not allowed to return a book you do not own!");

        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You are not the Owner of this book"));
        bookTransactionHistory.setReturnApproved(true);
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadCover(Integer bookId, MultipartFile file, Authentication connectedUser) {
        //checking if the book exists in our database
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var cover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(cover);
        bookRepository.save(book);
    }
}

