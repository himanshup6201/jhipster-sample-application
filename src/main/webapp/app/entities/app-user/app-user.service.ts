import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, Search } from 'app/shared/util/request-util';
import { IAppUser } from 'app/shared/model/app-user.model';

type EntityResponseType = HttpResponse<IAppUser>;
type EntityArrayResponseType = HttpResponse<IAppUser[]>;

@Injectable({ providedIn: 'root' })
export class AppUserService {
  public resourceUrl = SERVER_API_URL + 'api/app-users';
  public resourceSearchUrl = SERVER_API_URL + 'api/_search/app-users';

  constructor(protected http: HttpClient) {}

  create(appUser: IAppUser): Observable<EntityResponseType> {
    return this.http.post<IAppUser>(this.resourceUrl, appUser, { observe: 'response' });
  }

  update(appUser: IAppUser): Observable<EntityResponseType> {
    return this.http.put<IAppUser>(this.resourceUrl, appUser, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAppUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAppUser[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }
}
