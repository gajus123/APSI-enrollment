import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import * as interceptors from './interceptors';

@NgModule({
  declarations: [],
  imports: [CommonModule],
  providers: [...interceptors.providers],
})
export class CoreModule {}
