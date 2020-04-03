export interface UserCredentials {
  username: string;
  password: string;
}

export interface TokenBundle {
  tokenType: string;
  accessToken: string;
  refreshToken: string;
}
