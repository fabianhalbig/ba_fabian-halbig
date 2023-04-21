import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseDetailSubscriberComponent } from './course-detail-subscriber.component';

describe('CourseDetailSubscriberComponent', () => {
  let component: CourseDetailSubscriberComponent;
  let fixture: ComponentFixture<CourseDetailSubscriberComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CourseDetailSubscriberComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseDetailSubscriberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
