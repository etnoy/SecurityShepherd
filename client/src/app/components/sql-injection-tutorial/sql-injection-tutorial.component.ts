import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../../service/api.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Module } from 'src/app/model/module';

@Component({
  selector: 'app-sql-injection-tutorial',
  templateUrl: './sql-injection-tutorial.component.html',
  styleUrls: ['./sql-injection-tutorial.component.css']
})
export class SqlInjectionTutorialComponent implements OnInit {
  queryForm: FormGroup;
  queryResult: string[];

  @Input() module: Module;

  constructor(private apiService: ApiService, public fb: FormBuilder) {
    this.queryForm = this.fb.group({
      query: ['']
    });
    this.queryResult = [];
  }

  ngOnInit(): void {}

  submitQuery() {
    return this.apiService
      .modulePostRequest(this.module.id, 'query', this.queryForm.value)
      .subscribe((queryResult: string[]) => {
        console.log(queryResult);
        this.queryResult = queryResult;
      });
  }
}
