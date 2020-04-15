import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../../service/api.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Module } from 'src/app/model/module';

@Component({
  selector: 'app-xss-tutorial',
  templateUrl: './xss-tutorial.component.html',
  styleUrls: ['./xss-tutorial.component.css'],
})
export class XssTutorialComponent implements OnInit {
  queryForm: FormGroup;
  result: string;
  flag: string;
  alertFunction: any;

  @Input() module: Module;

  constructor(private apiService: ApiService, public fb: FormBuilder) {
    this.queryForm = this.fb.group({
      query: [''],
    });
    this.result = '';
    this.flag = '';
  }

  ngOnInit(): void {}

  submitQuery() {
    return this.apiService
      .modulePostRequest(this.module.id, 'query', this.queryForm.value)
      .subscribe((data) => {
        data = JSON.parse(data);
        this.result = data['result'];
        this.flag = data['flag'];
        const alert = data['alert'];
        if (alert) {
          window.alert(alert);
        }
      });
  }
}
