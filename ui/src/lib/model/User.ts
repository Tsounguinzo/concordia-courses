export interface UserId {
  id: string;
}

export type UserResponse = {
  user: UserId;
};

export type User = {
  username: UserId;
  email: string
  password: string
};