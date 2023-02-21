import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ActivatedRoute} from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms'

@Component({
  selector: 'app-task-create',
  templateUrl: './task-create.component.html',
  styleUrls: ['./task-create.component.scss']
})
export class TaskCreateComponent implements OnInit {

  course:any;
  taskForm:FormGroup;
  alert:boolean=false;
  error:boolean=false;

  constructor(public router: Router, private route: ActivatedRoute, private http:HttpClient, 
    private formBuilder: FormBuilder) { 
        this.taskForm = this.formBuilder.group({
        title: formBuilder.control('', [ Validators.required, Validators.minLength(8) , Validators.maxLength(32) ]), 
        description: formBuilder.control('', [ Validators.required, Validators.minLength(10) , Validators.maxLength(60) ]),
        deadline: formBuilder.control('', [ Validators.required ])
      })
    }

  ngOnInit(): void {
    let uuid = this.route.snapshot.paramMap.get('topicUuid')
    this.http.get("api/course/" + uuid).subscribe((data)=>this.course=data);
  }

  onSubmit() {
    const body = {
      title: this.taskForm.value.title,
      longDescription: this.taskForm.value.description,
      deadline: this.taskForm.value.deadline,
      course: {
          uuid: this.course.uuid,
          userId: this.course.userId,
          title: this.course.title,
          description: this.course.description,
          subscriber: this.course.subscriber
      }
    }
    this.http.post("api/course/task", body, {observe: 'response'}).subscribe(data => this.course = data, response => {
      if (response.status == "200") {
        this.alert=true;
      } else {
        this.error=true;
      }
    });
    this.taskForm.reset({})
  }

  public control(name: string) {
    return this.taskForm.get(name);
  }

  public backToCourse(): void {
    this.router.navigate(['course/creator/' + this.course.uuid]);
  }

}
