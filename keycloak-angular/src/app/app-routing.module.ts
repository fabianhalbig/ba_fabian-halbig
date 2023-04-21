import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { CourseCreateComponent } from './course-create/course-create.component';
import { CourseDetailCreatorComponent } from './course-detail-creator/course-detail-creator.component';
import { CourseDetailSubscriberComponent } from './course-detail-subscriber/course-detail-subscriber.component';
import { CourseComponent } from './course/course.component';
import { HomeComponent } from './home/home.component';
import { StatusComponent } from './status/status.component';
import { TaskCreateComponent } from './task-create/task-create.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'courses',
    component: CourseComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'status',
    component: StatusComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'course/creator/:uuid',
    component: CourseDetailCreatorComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'course/sub/:uuid',
    component: CourseDetailSubscriberComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'course/create',
    component: CourseCreateComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'task/create/:topicUuid',
    component: TaskCreateComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'status',
    component: StatusComponent,
    canActivate: [AuthGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
