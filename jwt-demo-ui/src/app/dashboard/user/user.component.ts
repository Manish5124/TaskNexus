import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { HelloService } from 'src/app/services/hello.service';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule, NavBarComponent],
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent {
  message =''
  constructor(private helloService: HelloService){
     helloService.helloUser().subscribe((res)=> {
        this.message = res.message
    })
  }
}
