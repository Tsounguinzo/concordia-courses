export interface UserId {
  id: string;
}

export type UserResponse = {
  user: UserId;
};

export type User = {
  username: string
  password: string
  email?: string
}
