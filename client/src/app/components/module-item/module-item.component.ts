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
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Module } from '../../model/module';
import { XssTutorialComponent } from '../xss-tutorial/xss-tutorial.component';
import { throwError } from 'rxjs';
import { AlertService } from 'src/app/service/alert.service';
import { Submission } from 'src/app/model/submission';

@Component({
  selector: 'app-module-item',
  templateUrl: './module-item.component.html',
  styleUrls: ['./module-item.component.css'],
})
export class ModuleItemComponent implements OnInit {
  flagForm: FormGroup;
  loading = false;
  submitted = false;
  userId: string;
  solved = false;

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
      flag: ['', Validators.required],
    });
    this.flagForm.enable();
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const shortName = params.get('shortName');
      this.apiService
        .getModuleByShortName(shortName)
        .subscribe((module: Module) => {
          this.module = module;
          this.solved = this.module.isSolved;
          if (this.solved) {
            this.flagForm.disable();
          }
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
              throwError('shortName cannot be resolved');
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
    this.submitted = true;

    // stop here if form is invalid
    if (this.flagForm.invalid) {
      return;
    }
    this.loading = true;

    return this.apiService
      .submitFlag(this.module.id, this.flagForm.controls.flag.value)
      .subscribe(
        (submission: Submission) => {
          this.loading = false;
          if (submission.isValid) {
            this.alertService.success(`Well done, module complete`);
            this.flagForm = this.fb.group({
              flag: [''],
            });
          } else {
            this.alertService.error(`Invalid flag`);
          }
        },
        (error) => {
          this.alertService.error(`An error occurred`);
        }
      );
  }
}
