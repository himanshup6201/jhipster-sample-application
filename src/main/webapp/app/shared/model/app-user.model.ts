import { IRole } from 'app/shared/model/role.model';

export interface IAppUser {
  id?: number;
  username?: string;
  password?: string;
  adminflag?: boolean;
  deleteflag?: boolean;
  roles?: IRole[];
}

export class AppUser implements IAppUser {
  constructor(
    public id?: number,
    public username?: string,
    public password?: string,
    public adminflag?: boolean,
    public deleteflag?: boolean,
    public roles?: IRole[]
  ) {
    this.adminflag = this.adminflag || false;
    this.deleteflag = this.deleteflag || false;
  }
}
