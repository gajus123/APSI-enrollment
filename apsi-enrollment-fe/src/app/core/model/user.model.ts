export enum UserRole {
  STUDENT = 'STUDENT',
  LECTURER = 'LECTURER',
}

export interface User {
  id: number;
  name: string;
  surname: string;
  email: string;
  username: string;
  userRoles: UserRole[];
}
