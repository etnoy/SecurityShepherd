import { Component, OnInit, Input } from '@angular/core';
import { ApiService } from '../../service/api.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Module } from 'src/app/model/module';
import { AlertService } from 'src/app/service/alert.service';

@Component({
  selector: 'app-xss-tutorial',
  templateUrl: './xss-tutorial.component.html',
  styleUrls: ['./xss-tutorial.component.css'],
})
export class XssTutorialComponent implements OnInit {
  queryForm: FormGroup;
  result: string;
  loading = false;

  @Input() module: Module;

  constructor(
    private apiService: ApiService,
    public fb: FormBuilder,
    private alertService: AlertService
  ) {
    this.queryForm = this.fb.group({
      query: [''],
    });
    this.result = '';
  }

  ngOnInit(): void {}

  submitQuery() {
    // stop here if form is invalid
    if (this.queryForm.invalid) {
      return;
    }
    this.loading = true;

    return this.apiService
      .modulePostRequest(
        this.module.id,
        'query',
        this.queryForm.controls.query.value
      )
      .subscribe((data) => {
        data = JSON.parse(data);
        this.result = data['result'];
        const flag = data['flag'];
        if (flag) {
          this.alertService.success(flag);
        }
        const alert = data['alert'];
        if (alert) {
          window.alert(alert);
        }
        this.loading = false;
      });
  }
}
