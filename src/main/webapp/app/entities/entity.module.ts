import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'app-user',
        loadChildren: () => import('./app-user/app-user.module').then(m => m.JhipsterSampleApplicationAppUserModule)
      },
      {
        path: 'role',
        loadChildren: () => import('./role/role.module').then(m => m.JhipsterSampleApplicationRoleModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class JhipsterSampleApplicationEntityModule {}
