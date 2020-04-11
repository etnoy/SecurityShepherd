import { Component, OnInit } from '@angular/core';
import { ApiService } from '../api.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-sql-injection-tutorial',
  templateUrl: './sql-injection-tutorial.component.html',
  styleUrls: ['./sql-injection-tutorial.component.css'],
})
export class SqlInjectionTutorialComponent implements OnInit {
  queryForm: FormGroup;
  queryResult: string[];

  constructor(private api: ApiService, public fb: FormBuilder) {
    this.queryForm = this.fb.group({
      query: [''],
    });
    this.queryResult = [];
  }

  ngOnInit(): void {}

  submitQuery() {
    return this.api
      .submitSqlInjectionTutorialQuery(this.queryForm.value)
      .subscribe((queryResult: string[]) => {
        console.log(queryResult);
        this.queryResult = queryResult;
      });
  }
}
