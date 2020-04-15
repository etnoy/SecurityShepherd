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

@Component({
  selector: 'app-module-item',
  templateUrl: './module-item.component.html',
  styleUrls: ['./module-item.component.css'],
})
export class ModuleItemComponent implements OnInit {
  flagForm: FormGroup;

  @Input() modules: Module[];

  @ViewChild(ModuleDirective) moduleDirective: ModuleDirective;

  module: Module;

  constructor(
    public fb: FormBuilder,
    private route: ActivatedRoute,
    private componentFactoryResolver: ComponentFactoryResolver,
    public apiService: ApiService
  ) {
    this.flagForm = this.fb.group({
      flag: [''],
    });
  }
  ngOnInit() {
    this.route.paramMap.subscribe((params) => {
      const moduleId = params.get('id');
      this.apiService.getModuleById(moduleId).subscribe((module: Module) => {
        this.module = module;
        let currentModule;
        switch (this.module.url) {
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
        const componentRef = viewContainerRef.createComponent(componentFactory);

        (componentRef.instance as typeof currentModule).module = this.module;
      });
    });
  }

  submitFlag() {}
}
