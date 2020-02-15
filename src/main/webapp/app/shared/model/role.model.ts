import { IAppUser } from 'app/shared/model/app-user.model';

export interface IRole {
  id?: number;
  role?: string;
  appusers?: IAppUser[];
}

export class Role implements IRole {
  constructor(public id?: number, public role?: string, public appusers?: IAppUser[]) {}
}
