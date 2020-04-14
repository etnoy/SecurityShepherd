import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { XssTutorialComponent } from './xss-tutorial.component';

describe('XssTutorialComponent', () => {
  let component: XssTutorialComponent;
  let fixture: ComponentFixture<XssTutorialComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ XssTutorialComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(XssTutorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
