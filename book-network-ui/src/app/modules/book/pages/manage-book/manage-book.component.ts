import {Component, OnInit} from '@angular/core';
import {BookRequest} from "../../../../services/models/book-request";
import {BookService} from "../../../../services/services/book.service";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-manage-book',
  templateUrl: './manage-book.component.html',
  styleUrls: ['./manage-book.component.scss']
})
export class ManageBookComponent implements OnInit {
  errorMsg: Array<string> = [];
  selectedBookCover: any;
  selectedPicture: string | undefined;
  bookRequest: BookRequest = {authorName: "", isbn: "", synopsis: "", title: ""};


  constructor(private bookService: BookService,
              private router: Router,
              private activatedRoute: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    const bookId = this.activatedRoute.snapshot.params["bookId"];
    if (bookId) {
      this.bookService.findBookById({"id": bookId}).subscribe({
        next: (book) => {
          this.bookRequest = {
            id: book.id,
            authorName: book.authorName as string,
            isbn: book.isbn as string,
            synopsis: book.synopsis as string,
            title: book.title as string,
            shareable: book.shareable as boolean
          }
          if (book.cover) {
            this.selectedPicture = 'data:image/png;base64,' + book.cover;
          }
        }
      });
    }
  }

  onFileSelected(selectedImg: any) {
    this.selectedBookCover = selectedImg.target.files[0];
    if (this.selectedBookCover) {
      const reader = new FileReader();
      reader.readAsDataURL(this.selectedBookCover);
      reader.onload = () => {
        this.selectedPicture = reader.result as string
      }
      reader.readAsDataURL(this.selectedBookCover);
    }
  }

  saveBook() {
    this.bookService.saveBook({
      body: this.bookRequest
    }).subscribe({
      next: (bookId) => {
        this.bookService.uploadCover({
          "book-id": bookId,
          body: {
            file: this.selectedBookCover
          }
        }).subscribe({
          next: () => {
            this.router.navigate(["/books/my-books"])
          }
        })
      },
      error: (err) => {
        this.errorMsg = err.error.validationErrors;
      }
    })
  }
}
