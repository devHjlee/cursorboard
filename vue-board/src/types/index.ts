export interface User {
  email: string;
  token: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  authorEmail: string;
  comments: Comment[];
  createdAt: string;
  updatedAt: string;
}

export interface Comment {
  id: number;
  content: string;
  authorEmail: string;
  createdAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface PostRequest {
  title: string;
  content: string;
}

export interface CommentRequest {
  content: string;
}

export interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
} 