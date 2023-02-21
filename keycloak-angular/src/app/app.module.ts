import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { initializer } from 'src/utils/app-inits';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { StatusComponent } from './status/status.component';
import { HomeComponent } from './home/home.component';
import { CourseComponent } from './course/course.component';
import { CourseDetailCreatorComponent } from './course-detail-creator/course-detail-creator.component';
import { CourseCreateComponent } from './course-create/course-create.component';
import { TaskCreateComponent } from './task-create/task-create.component';
import { CourseDetailSubscriberComponent } from './course-detail-subscriber/course-detail-subscriber.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    AppComponent,
    StatusComponent,
    HomeComponent,
    CourseComponent,
    CourseDetailCreatorComponent,
    CourseCreateComponent,
    TaskCreateComponent,
    CourseDetailSubscriberComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    HttpClientModule,
    ReactiveFormsModule,
    NgbModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      deps: [KeycloakService],
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
