import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { environment } from 'src/environments/environment.prod';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  user: any;
  abo: any;
  aboForm: FormGroup;
  alert = false;
  error = false;
  constructor(
    public router: Router,
    private http: HttpClient,
    private formBuilder: FormBuilder
  ) {
    this.aboForm = this.formBuilder.group({
      aboUuid: formBuilder.control(''),
    });
  }

  ngOnInit(): void {
    this.http
      .get('/api/user/current/details')
      .subscribe((data) => (this.user = data));
  }

  public onSubmit() {
    const body = {};
    this.http
      .post('api/course/subscribe/' + this.aboForm.value.aboUuid, body, {
        observe: 'response',
      })
      .subscribe(
        (data) => (this.abo = data),
        (response) => {
          if (response.status == '200') {
            this.alert = true;
          } else {
            this.error = true;
          }
        }
      );
  }

  public goToPublic() {
    window.location.href = environment.publicAngular;
  }
}
