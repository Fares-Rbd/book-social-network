import {Component} from '@angular/core';
import {PageResponseBorrowedBookResponse} from "../../../../services/models/page-response-borrowed-book-response";
import {BorrowedBookResponse} from "../../../../services/models/borrowed-book-response";
import {BookService} from "../../../../services/services/book.service";

@Component({
  selector: 'app-returned-books',
  templateUrl: './returned-books.component.html',
  styleUrls: ['./returned-books.component.scss']
})
export class ReturnedBooksComponent {
  returnedBooks: PageResponseBorrowedBookResponse = {};
  page = 0;
  size = 5;
  selectedBook: BorrowedBookResponse | undefined = undefined;
  message: string = '';
  type: string = 'success';

  constructor(private bookService: BookService) {
  }

  get isLastPage()
    :
    boolean {
    return this.page == this.returnedBooks.totalPages as number - 1;
  }

  ngOnInit(): void {
    this.findAllReturned();
  }


  findAllReturned() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response) => {
        this.returnedBooks = response;
      }
    });
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllReturned()
  }

  goToPreviousPage() {
    this.page--;
    this.findAllReturned();
  }

  goToPage(pageIndex
             :
             number
  ) {
    this.page = pageIndex;
    this.findAllReturned();
  }

  goToNextPage() {
    this.page++;
    this.findAllReturned();
  }

  goToLastPage() {
    this.page = this.returnedBooks.totalPages as number - 1;
    this.findAllReturned();
  }

  approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned) {
      this.type = 'error';
      this.message = 'Book is not returned yet!';
      return;
    }
    this.bookService.approveReturnedBook({
      "book-id": book.id as number
    }).subscribe({
      next: () => {
        this.type = 'success';
        this.message = 'Book return approved successfully!';
        this.findAllReturned();
      }
    });
  }
}
