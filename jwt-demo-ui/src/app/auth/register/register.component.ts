import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { RegisterRequest } from 'src/app/models/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
registerForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required]),
    role: new FormControl('USER', [Validators.required])
  })
  errorMessage: string =''

  constructor(private authService: AuthService, private router: Router) {}

  handleSubmit(){
    this.errorMessage =''
    if(this.registerForm.valid){
      const registerRequest: RegisterRequest = {
        username: this.registerForm.value.username!,
        password: this.registerForm.value.password!,
        role: this.registerForm.value.role!
      }
      this.authService.register(registerRequest).subscribe({
        next: res => {
        if(res.message){
          this.router.navigate(['/login'])
        }
      },
      error: () => {
        this.errorMessage ='Registration failed, please try again'
      }
    })
    }else{
      this.errorMessage ='Invalid username / password'
    }
  }
}
