import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { JhipsterSampleApplicationTestModule } from '../../../test.module';
import { AppUserUpdateComponent } from 'app/entities/app-user/app-user-update.component';
import { AppUserService } from 'app/entities/app-user/app-user.service';
import { AppUser } from 'app/shared/model/app-user.model';

describe('Component Tests', () => {
  describe('AppUser Management Update Component', () => {
    let comp: AppUserUpdateComponent;
    let fixture: ComponentFixture<AppUserUpdateComponent>;
    let service: AppUserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [JhipsterSampleApplicationTestModule],
        declarations: [AppUserUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AppUserUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppUserUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AppUserService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new AppUser(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new AppUser();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
