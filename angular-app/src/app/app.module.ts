import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DepartmentsModule} from './departments/departments.module';
import {EmployeesModule} from './employees/employees.module';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {NavigationComponent} from './components/navigation/navigation.component';

@NgModule({
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    NavigationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    DepartmentsModule,
    EmployeesModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
