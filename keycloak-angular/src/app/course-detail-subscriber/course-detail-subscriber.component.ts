import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-course-detail-subscriber',
  templateUrl: './course-detail-subscriber.component.html',
  styleUrls: ['./course-detail-subscriber.component.scss'],
})
export class CourseDetailSubscriberComponent implements OnInit {
  statusOfTasks = new Map<string, string[]>();
  course: any;
  status: any;
  accessDenied: boolean = false;

  constructor(
    public router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    let uuid = this.route.snapshot.paramMap.get('uuid');

    //Check access via API response
    this.http.get('api/course/' + uuid, { observe: 'response' }).subscribe(
      (data) => null,
      (response) => {
        if (response.status == '403') {
          this.accessDenied = true;
        }
      }
    );

    //Get Data if access is valid
    if (this.accessDenied == false) {
      this.getData(uuid);
    }
  }

  reopenTask(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/reopen', null)
      .subscribe((data) => (this.status = data));
    this.getData(this.course.uuid);
  }

  setStatusToDone(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/finish', null)
      .subscribe((data) => (this.status = data));
    this.getData(this.course.uuid);
  }

  setStatusInProgress(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/progress', null)
      .subscribe((data) => (this.status = data));
    this.getData(this.course.uuid);
  }

  getData(uuid: String) {
    this.http.get('api/course/' + uuid).subscribe((courseData) => {
      this.course = courseData;
      let tasks = courseData['tasks'];
      this.http.get('api/user/status/currentuser').subscribe((statusData) => {
        let statusOftask;
        let statusIdOftask;
        for (let i = 0; i < Object.keys(tasks).length; i++) {
          for (let j = 0; j < Object.keys(statusData).length; j++) {
            if (tasks[i]['id'] == statusData[j]['task']) {
              statusOftask = statusData[j]['status'];
              statusIdOftask = statusData[j]['id'];
              this.statusOfTasks.set(tasks[i]['id'], [
                tasks[i]['title'],
                tasks[i]['longDescription'],
                tasks[i]['deadline'],
                statusOftask,
                statusIdOftask,
              ]);
              statusOftask = '';
            }
            continue;
          }
        }
      });
    });
  }
}
