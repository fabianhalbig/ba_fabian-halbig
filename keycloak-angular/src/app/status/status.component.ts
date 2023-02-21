import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-status',
  templateUrl: './status.component.html',
  styleUrls: ['./status.component.scss'],
})
export class StatusComponent implements OnInit {
  tasksWithStatus: any;
  status: any;
  statusOfTasks = new Map<string, string[]>();

  constructor(public router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    this.getData();
  }

  reopenTask(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/reopen', null)
      .subscribe((data) => (this.status = data));
    this.getData();
  }

  setStatusToDone(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/finish', null)
      .subscribe((data) => (this.status = data));
    this.getData();
  }

  setStatusInProgress(statusId: String) {
    this.http
      .post('api/user/status/' + statusId + '/progress', null)
      .subscribe((data) => (this.status = data));
    this.getData();
  }

  getData() {
    this.http.get('api/user/status/currentuser').subscribe((statusData) => {
      for (let i = 0; i < Object.keys(statusData).length; i++) {
        this.http
          .get('api/course/task/' + statusData[i]['task'])
          .subscribe((taskData) => {
            this.statusOfTasks.set(statusData[i]['id'], [
              taskData['title'],
              taskData['longDescription'],
              taskData['deadline'],
              statusData[i]['status'],
              statusData[i]['id'],
            ]);
          });
      }
    });
  }
}
