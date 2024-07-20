import {Component, OnInit} from '@angular/core';
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {BookResponse} from "../../../../services/models/book-response";

@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrls: ['./my-books.component.scss']
})
export class MyBooksComponent implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  page: number = 0; //display first page
  size: number = 4;

  constructor(
    private bookService: BookService,
    private router: Router,
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


  findAllBooks() {
    this.bookService.findAllBooksByOwner({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books) => {
        this.bookResponse = books;
      }
    })
  }

  shareBook(book: BookResponse) {
    this.bookService.updateShareableStatus({"book-id": book.id as number}).subscribe({
      next: () => {
        book.shareable = !book.shareable;
      }
    });
  }


  editBook(book: BookResponse) {
    this.router.navigate(["/books/manage", book.id])
  }

  archiveBook(book: BookResponse) {
    this.bookService.updateArchivedStatus({"book-id": book.id as number}).subscribe({
      next: () => {
        book.archived = !book.archived;
      }
    });
  }
}
