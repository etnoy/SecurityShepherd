import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SqlInjectionTutorialComponent } from './sql-injection-tutorial.component';

describe('SqlInjectionTutorialComponent', () => {
  let component: SqlInjectionTutorialComponent;
  let fixture: ComponentFixture<SqlInjectionTutorialComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SqlInjectionTutorialComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SqlInjectionTutorialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
