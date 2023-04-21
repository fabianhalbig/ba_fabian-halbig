import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-course-create',
  templateUrl: './course-create.component.html',
  styleUrls: ['./course-create.component.scss']
})
export class CourseCreateComponent implements OnInit {

  course:any;
  courseForm:FormGroup;
  alert:boolean=false;
  error:boolean=false;


  constructor(public router: Router, private http:HttpClient, private formBuilder: FormBuilder) { 
    this.courseForm = this.formBuilder.group({
      title: formBuilder.control('', [ Validators.required, Validators.minLength(10) , Validators.maxLength(60) ]), 
      description: formBuilder.control('', [ Validators.required, Validators.minLength(10), Validators.maxLength(120)])
    });
  }

  ngOnInit(): void {   
  }

  public onSubmit() {
    const body = { 
      title: this.courseForm.value.title,
      description: this.courseForm.value.description
    };
    this.http.post("api/course", body, {observe: 'response'})
    .subscribe(data =>this.course = data, response => {
      if (response.status=="200") {
        this.alert=true;
      } else {
        this.error=true;
      }
    })

    this.courseForm.reset({})
  }


  //Validator in HTML
  public control(name: string) {
    return this.courseForm.get(name);
  }

}


