import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRole } from 'app/shared/model/role.model';
import { RoleService } from './role.service';
import { RoleDeleteDialogComponent } from './role-delete-dialog.component';

@Component({
  selector: 'jhi-role',
  templateUrl: './role.component.html'
})
export class RoleComponent implements OnInit, OnDestroy {
  roles?: IRole[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected roleService: RoleService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.roleService
        .search({
          query: this.currentSearch
        })
        .subscribe((res: HttpResponse<IRole[]>) => (this.roles = res.body || []));
      return;
    }

    this.roleService.query().subscribe((res: HttpResponse<IRole[]>) => (this.roles = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInRoles();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IRole): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInRoles(): void {
    this.eventSubscriber = this.eventManager.subscribe('roleListModification', () => this.loadAll());
  }

  delete(role: IRole): void {
    const modalRef = this.modalService.open(RoleDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.role = role;
  }
}
