import {Component, OnInit} from '@angular/core';
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";
import {BookService} from "../../../../services/services/book.service";
import {FeedbackRequest} from "../../../../services/models/feedback-request";
import {FeedbackService} from "../../../../services/services/feedback.service";

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.scss']
})
export class BorrowedBookListComponent implements OnInit {

  borrowedBooks: PageResponseBorrowedBookResponse = {};
  feedbackRequest: FeedbackRequest = {
    bookId: 0,
    comment: '',
    rating: 0
  };
  page = 0;
  size = 5;
  selectedBook: BorrowedBookResponse | undefined = undefined;

  constructor(private bookService: BookService,
              private feedbackService: FeedbackService
  ) {
  }

  get isLastPage()
    :
    boolean {
    return this.page == this.borrowedBooks.totalPages as number - 1;
  }

  ngOnInit()
    :
    void {
    this.findAllBorrowedBooks();
  }

  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;

  }

  findAllBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        this.borrowedBooks = response;
      }
    });
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks()
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBorrowedBooks();
  }

  goToPage(pageIndex
             :
             number
  ) {
    this.page = pageIndex;
    this.findAllBorrowedBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBorrowedBooks();
  }

  goToLastPage() {
    this.page = this.borrowedBooks.totalPages as number - 1;
    this.findAllBorrowedBooks();
  }

  returnBook(withFeedback
               :
               boolean
  ) {
    this.bookService.returnBook({"book-id": this.selectedBook?.id as number}).subscribe({
      next: () => {
        if (withFeedback) {
          this.giveFeedback();
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBooks();
      }
    });
  }

  giveFeedback() {
    this.feedbackService.saveFeedback({body: this.feedbackRequest}).subscribe({
      next: () => {
      }
    });
  }
}
