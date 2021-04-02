import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {DepartmentsRoutingModule} from './departments-routing.module';
import {DepartmentListComponent} from './components/department-list/department-list.component';
import {DepartmentFormComponent} from './components/department-form/department-form.component';
import {DepartmentsComponent} from './departments.component';


@NgModule({
  declarations: [DepartmentListComponent, DepartmentFormComponent, DepartmentsComponent],
  imports: [
    CommonModule,
    DepartmentsRoutingModule
  ]
})
export class DepartmentsModule { }
