import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {BookResponse} from "../../../../services/models/book-response";
import {AuthenticationService} from "../../../../services/services/authentication.service";

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {

  bookResponse: PageResponseBookResponse = {};
  page: number = 0; //display first page
  size: number = 4;
  message: string = '';
  type: string = 'success';

  constructor(
    private bookService: BookService,
    private router: Router,
    private authService: AuthenticationService
  ) {
  }

  get isLastPage(): boolean {
    return this.page == this.bookResponse.totalPages as number - 1;
  }

  ngOnInit(): void {

    this.findAllBooks();
  }

  goToFirstPage() {
    this.page = 0;
    this.findAllBooks()
  }

  goToPreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  goToPage(pageIndex: number) {
    this.page = pageIndex;
    this.findAllBooks();
  }

  goToNextPage() {
    this.page++;
    this.findAllBooks();
  }

  goToLastPage() {
    this.page = this.bookResponse.totalPages as number - 1;
    this.findAllBooks();
  }

  borrowBook(book: BookResponse) {
    console.log("this is the book id: ", book.id)
    this.bookService.borrowBook({
      'id': book.id as number
    }).subscribe({
      next: () => {
        this.message = 'Book borrowed successfully';
        this.type = 'success';
      },
      error: (err) => {
        console.log(err.error.error);
        this.message = err.error.error;
        this.type = 'error';
      }

    });
  }

  findAllBooks() {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books) => {
        this.bookResponse = books;
      }
    })
  }
}
