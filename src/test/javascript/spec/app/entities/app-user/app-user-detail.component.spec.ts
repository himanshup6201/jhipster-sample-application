import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { JhipsterSampleApplicationTestModule } from '../../../test.module';
import { AppUserDetailComponent } from 'app/entities/app-user/app-user-detail.component';
import { AppUser } from 'app/shared/model/app-user.model';

describe('Component Tests', () => {
  describe('AppUser Management Detail Component', () => {
    let comp: AppUserDetailComponent;
    let fixture: ComponentFixture<AppUserDetailComponent>;
    const route = ({ data: of({ appUser: new AppUser(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterSampleApplicationTestModule],
        declarations: [AppUserDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(AppUserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AppUserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load appUser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.appUser).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
