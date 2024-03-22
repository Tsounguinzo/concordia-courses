export interface UserId {
  id: string;
}

export type User = {
  username: UserId;
  email: string
  password: string
};