import { Routes } from "@angular/router";
import { LoginComponent } from "./auth/login/login.component";
import { RegisterComponent } from "./auth/register/register.component";
import { UserComponent } from "./dashboard/user/user.component";
import { AdminComponent } from "./dashboard/admin/admin.component";
import { AuthGuard } from "./guards/auth.guard";
import { RoleGuard } from "./guards/role.guard";

export const routes: Routes = [
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'user', 
    canActivate:[AuthGuard, RoleGuard], 
    data: {roles: ['USER', 'ADMIN'] },  
    component: UserComponent},
  {path: 'admin', 
    canActivate: [AuthGuard, RoleGuard], 
    data: {roles: ['ADMIN'] }, 
    component: AdminComponent}
];

