import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Routes, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { flatMap } from 'rxjs/operators';

import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { IAppUser, AppUser } from 'app/shared/model/app-user.model';
import { AppUserService } from './app-user.service';
import { AppUserComponent } from './app-user.component';
import { AppUserDetailComponent } from './app-user-detail.component';
import { AppUserUpdateComponent } from './app-user-update.component';

@Injectable({ providedIn: 'root' })
export class AppUserResolve implements Resolve<IAppUser> {
  constructor(private service: AppUserService, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAppUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        flatMap((appUser: HttpResponse<AppUser>) => {
          if (appUser.body) {
            return of(appUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new AppUser());
  }
}

export const appUserRoute: Routes = [
  {
    path: '',
    component: AppUserComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'AppUsers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: AppUserDetailComponent,
    resolve: {
      appUser: AppUserResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'AppUsers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: AppUserUpdateComponent,
    resolve: {
      appUser: AppUserResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'AppUsers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: AppUserUpdateComponent,
    resolve: {
      appUser: AppUserResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'AppUsers'
    },
    canActivate: [UserRouteAccessService]
  }
];
