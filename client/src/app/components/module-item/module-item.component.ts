import { ModuleDirective } from '../../module.directive';
import { SqlInjectionTutorialComponent } from '../sql-injection-tutorial/sql-injection-tutorial.component';
import { ApiService } from '../../service/api.service';
import {
  Component,
  OnInit,
  Input,
  ViewChild,
  ComponentFactoryResolver,
} from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Module } from '../../model/module';
import { XssTutorialComponent } from '../xss-tutorial/xss-tutorial.component';
import { throwError } from 'rxjs';
import { AlertService } from 'src/app/service/alert.service';

@Component({
  selector: 'app-module-item',
  templateUrl: './module-item.component.html',
  styleUrls: ['./module-item.component.css'],
})
export class ModuleItemComponent implements OnInit {
  flagForm: FormGroup;
  loading = false;
  submitted = false;

  @Input() modules: Module[];

  @ViewChild(ModuleDirective) moduleDirective: ModuleDirective;

  module: Module;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private componentFactoryResolver: ComponentFactoryResolver,
    private apiService: ApiService,
    private alertService: AlertService
  ) {
    this.flagForm = this.fb.group({
      flag: [''],
    });
  }
  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const shortName = params.get('shortName');
      console.log(shortName);
      this.apiService
        .getModuleByShortName(shortName)
        .subscribe((module: Module) => {
          this.module = module;
          console.log(module);

          let currentModule;
          switch (this.module.shortName) {
            case 'sql-injection-tutorial': {
              currentModule = SqlInjectionTutorialComponent;
              break;
            }
            case 'xss-tutorial': {
              currentModule = XssTutorialComponent;
              break;
            }
            default: {
              throwError('url cannot be resolved');
              break;
            }
          }
          const componentFactory = this.componentFactoryResolver.resolveComponentFactory(
            currentModule
          );

          const viewContainerRef = this.moduleDirective.viewContainerRef;
          viewContainerRef.clear();
          const componentRef = viewContainerRef.createComponent(
            componentFactory
          );

          (componentRef.instance as typeof currentModule).module = this.module;
        });
    });
  }

  submitFlag() {
    this.loading = true;
    this.submitted = true;
    return this.apiService
      .modulePostRequest(this.module.id, 'submit', this.flagForm.value)
      .subscribe((data) => {
        this.loading = false;
        data = JSON.parse(data);
        const validSubmission = data['isValid'];
        const flag = data['flag'];

        if (validSubmission) {
          this.alertService.success(`Well done, flag ${flag} was correct.`);
        } else {
          this.alertService.error(`Invalid flag.`);
        }
        console.log(data);
      });
  }
}
