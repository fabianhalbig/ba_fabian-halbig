import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-course-detail-creator',
  templateUrl: './course-detail-creator.component.html',
  styleUrls: ['./course-detail-creator.component.scss'],
})
export class CourseDetailCreatorComponent implements OnInit {
  course: any;
  subscriberList: any;
  subscriber = new Map<string, string[]>();
  status: any;
  accessDenied: boolean = false;
  alert: boolean = false;
  error: boolean = false;

  constructor(
    public router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    let uuid = this.route.snapshot.paramMap.get('uuid');
    this.http.get('api/course/' + uuid, { observe: 'response' }).subscribe(
      (data) => null,
      (response) => {
        if (response.status == '403') {
          this.accessDenied = true;
        }
      }
    );
    if (this.accessDenied == false) {
      this.http.get('api/course/' + uuid).subscribe((data) => {
        this.course = data;
        this.http
          .get('api/user/status/' + uuid + '/creator')
          .subscribe((statusData) => {
            String(data['subscriber'])
              .split(',')
              .forEach((element) => {
                if (element != '') {
                  let numberFinishedTasks = 0;
                  for (let i = 0; i < Object.keys(statusData).length; i++) {
                    if (
                      statusData[i]['user'] == element &&
                      statusData[i]['status'] == 'FERTIG'
                    ) {
                      numberFinishedTasks = numberFinishedTasks + 1;
                    }
                  }
                  this.status = statusData;
                  this.subscriber.set(element, [String(numberFinishedTasks)]);
                }
              });
          });
      });
    }
  }

  deleteTask(id: String): void {
    let uuid = this.route.snapshot.paramMap.get('uuid');
    this.http
      .delete('/api/course/task/' + id, { observe: 'response' })
      .subscribe(
        (data) => (this.course = data),
        (response) => {
          if (response.status == '200') {
            this.alert = true;
          } else {
            this.error = true;
          }
        }
      );
    this.http
      .get('api/course/' + uuid)
      .subscribe((data) => (this.course = data));
  }

  showTaskCreation(uuid: String) {
    this.router.navigate(['task/create/' + uuid]);
  }
}
