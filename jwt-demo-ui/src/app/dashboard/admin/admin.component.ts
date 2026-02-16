import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from "../nav-bar/nav-bar.component";
import { HelloService } from 'src/app/services/hello.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, NavBarComponent],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent {
  message =''
  constructor(private helloService: HelloService){
     helloService.helloAdmin().subscribe((res)=> {
        this.message = res.message
    })
  }
}
