import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { JhipsterSampleApplicationTestModule } from '../../../test.module';
import { AppUserComponent } from 'app/entities/app-user/app-user.component';
import { AppUserService } from 'app/entities/app-user/app-user.service';
import { AppUser } from 'app/shared/model/app-user.model';

describe('Component Tests', () => {
  describe('AppUser Management Component', () => {
    let comp: AppUserComponent;
    let fixture: ComponentFixture<AppUserComponent>;
    let service: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterSampleApplicationTestModule],
        declarations: [AppUserComponent]
      })
        .overrideTemplate(AppUserComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AppUserService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new AppUser(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.appUsers && comp.appUsers[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
