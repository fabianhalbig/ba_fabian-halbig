import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseDetailCreatorComponent } from './course-detail-creator.component';

describe('CourseDetailCreatorComponent', () => {
  let component: CourseDetailCreatorComponent;
  let fixture: ComponentFixture<CourseDetailCreatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CourseDetailCreatorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseDetailCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
