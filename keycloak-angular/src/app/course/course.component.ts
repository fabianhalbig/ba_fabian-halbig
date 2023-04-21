import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-course',
  templateUrl: './course.component.html',
  styleUrls: ['./course.component.scss'],
})
export class CourseComponent implements OnInit {
  CreatedCoursesOfUser: any;
  SubscribedCoursesOfUser: any;
  alertDelete: boolean = false;
  errorDelete: boolean = false;
  alertDeabo: boolean = false;
  errorDeabo: boolean = false;
  closeResult = '';
  deleteId: String;
  deleteTitle: String;
  deaboId: String;
  deaboTitle: String;

  constructor(
    public router: Router,
    private http: HttpClient,
    private modalService: NgbModal
  ) {}

  ngOnInit(): void {
    this.http
      .get('/api/course/created')
      .subscribe((data) => (this.CreatedCoursesOfUser = data));
    this.http
      .get('/api/course/subscribed')
      .subscribe((data) => (this.SubscribedCoursesOfUser = data));
  }

  showCourseCreatorView(uuid: String): void {
    this.router.navigate(['course/creator/' + uuid]);
  }

  showCourseSubView(uuid: String): void {
    this.router.navigate(['course/sub/' + uuid]);
  }

  createCourse(): void {
    this.router.navigate(['course/create/']);
  }

  openDelete(content, uuid: String, title: String) {
    this.deleteId = uuid;
    this.deleteTitle = title;
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed`;
        }
      );
  }

  closeModalDelete() {
    this.http
      .delete('/api/course/' + this.deleteId, { observe: 'response' })
      .subscribe(
        (data) => (this.CreatedCoursesOfUser = data),
        (response) => {
          if (response.status == '200') {
            this.alertDelete = true;
          } else {
            this.errorDelete = true;
          }
        }
      );
    this.modalService.dismissAll();
    this.deleteId = '';
    this.deleteTitle = '';
    this.http
      .get('api/course/created')
      .subscribe((data) => (this.CreatedCoursesOfUser = data));
  }

  openDeabo(content, uuid: String, title: String) {
    this.deaboId = uuid;
    this.deaboTitle = title;
    this.modalService
      .open(content, { ariaLabelledBy: 'modal-basic-title' })
      .result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed`;
        }
      );
  }

  closeModalDeabo() {
    this.http
      .delete('api/course/reject/' + this.deaboId, { observe: 'response' })
      .subscribe(
        (data) => (this.CreatedCoursesOfUser = data),
        (response) => {
          if (response.status == '200') {
            this.alertDeabo = true;
          } else {
            this.errorDeabo = true;
          }
        }
      );
    this.modalService.dismissAll();
    this.deaboId = '';
    this.deaboTitle = '';
    this.http
      .get('api/course/subscribed')
      .subscribe((data) => (this.SubscribedCoursesOfUser = data));
  }
}
