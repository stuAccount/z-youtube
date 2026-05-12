export type VideoStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";
export type VideoVisibility = "PUBLIC" | "PRIVATE" | "UNLISTED";
export type ReactionType = "LIKE" | "DISLIKE";

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Page<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface AccountSummary {
  id: number;
  username: string;
  nickname: string | null;
  avatarUrl: string | null;
}

export interface SelfProfile {
  id: number;
  username: string;
  email: string;
  nickname: string | null;
  avatarUrl: string | null;
  bio: string | null;
  subscriberCount: number;
  subscriptionCount: number;
}

export interface PublicProfile {
  id: number;
  username: string;
  nickname: string | null;
  avatarUrl: string | null;
  bio: string | null;
  subscriberCount: number;
  subscriptionCount: number;
  subscribedByCurrentUser: boolean;
}

export interface LoginResponse extends SelfProfile {}

export interface PublicVideoSummary {
  id: number;
  title: string;
  videoUrl: string;
  coverUrl: string | null;
  status: VideoStatus;
  visibility: VideoVisibility;
  author: AccountSummary;
  createdAt: string;
}

export interface MyVideoSummary {
  id: number;
  title: string;
  videoUrl: string;
  coverUrl: string | null;
  status: VideoStatus;
  visibility: VideoVisibility;
  createdAt: string;
  updatedAt: string;
}

export interface FavoriteVideoSummary {
  id: number;
  title: string;
  status: VideoStatus;
  visibility: VideoVisibility;
  author: AccountSummary;
  createdAt: string;
  favoritedAt: string;
}

export interface VideoDetail {
  id: number;
  title: string;
  description: string;
  videoUrl: string;
  coverUrl: string | null;
  status: VideoStatus;
  visibility: VideoVisibility;
  author: AccountSummary;
  createdAt: string;
  viewCount: number;
  likeCount: number;
  dislikeCount: number;
  favoriteCount: number;
  myReaction: ReactionType | null;
  favorited: boolean;
}

export interface VideoEngagement {
  videoId: number;
  likeCount: number;
  dislikeCount: number;
  favoriteCount: number;
  myReaction: ReactionType | null;
  favorited: boolean;
}

export interface VideoViewCount {
  videoId: number;
  viewCount: number;
}

export interface CommentSummary {
  id: number;
  content: string;
  author: AccountSummary;
  createdAt: string;
}

export interface CommentDetail extends CommentSummary {
  videoId: number;
}

export interface AccountSubscription {
  targetAccountId: number;
  targetUsername: string;
  subscribed: boolean;
  subscriberCount: number;
}

export interface CreateVideoPayload {
  title: string;
  description: string;
  videoUrl: string;
  coverUrl?: string | null;
  visibility?: VideoVisibility;
}

export interface UpdateVideoPayload {
  title?: string;
  description?: string;
  videoUrl?: string;
  coverUrl?: string | null;
  visibility?: VideoVisibility;
}
