import { jsonBody, request } from "./client";
import type {
  AccountSubscription,
  CommentDetail,
  CommentSummary,
  CreateVideoPayload,
  FavoriteVideoSummary,
  LoginResponse,
  MyVideoSummary,
  Page,
  PublicProfile,
  PublicVideoSummary,
  ReactionType,
  SelfProfile,
  UpdateVideoPayload,
  VideoDetail,
  VideoEngagement,
  VideoStatus,
  VideoViewCount,
  VideoVisibility,
} from "./types";

export const api = {
  ping: () =>
    request<string>("/ping", {
      expectJson: false,
    }),

  login: (payload: { loginId: string; password: string }) =>
    request<LoginResponse>("/api/auth/login", {
      method: "POST",
      body: jsonBody(payload),
    }),
  me: () => request<{ id: number }>("/api/auth/me"),
  logout: () =>
    request<null>("/api/auth/logout", {
      method: "POST",
    }),
  register: (payload: { username: string; email: string; password: string }) =>
    request<SelfProfile>("/api/accounts/register", {
      method: "POST",
      body: jsonBody(payload),
    }),

  selfProfile: () => request<SelfProfile>("/api/accounts/profile"),
  publicProfile: (username: string) =>
    request<PublicProfile>(`/api/accounts/profile/${encodeURIComponent(username)}`),
  updateProfile: (payload: Partial<Pick<SelfProfile, "email" | "nickname" | "avatarUrl" | "bio">>) =>
    request<SelfProfile>("/api/accounts/profile", {
      method: "PATCH",
      body: jsonBody(payload),
    }),
  changePassword: (payload: { oldPassword: string; newPassword: string; confirmPassword: string }) =>
    request<null>("/api/accounts/password", {
      method: "PATCH",
      body: jsonBody(payload),
    }),
  withdraw: () =>
    request<null>("/api/accounts", {
      method: "DELETE",
    }),

  latestFeed: (page = 0, size = 12) =>
    request<Page<PublicVideoSummary>>("/api/feed/latest", { query: { page, size } }),
  videos: (params: {
    authorId?: number;
    keyword?: string;
    page?: number;
    size?: number;
    status?: "PUBLISHED";
    visibility?: "PUBLIC";
  }) => request<Page<PublicVideoSummary>>("/api/videos", { query: params }),
  videoDetail: (id: number) => request<VideoDetail>(`/api/videos/${id}`),
  recordView: (id: number) =>
    request<VideoViewCount>(`/api/videos/${id}/view`, {
      method: "POST",
    }),
  createVideo: (payload: CreateVideoPayload) =>
    request<VideoDetail>("/api/videos", {
      method: "POST",
      body: jsonBody(payload),
    }),
  updateVideo: (id: number, payload: UpdateVideoPayload) =>
    request<VideoDetail>(`/api/videos/${id}`, {
      method: "PATCH",
      body: jsonBody(payload),
    }),
  publishVideo: (id: number) =>
    request<VideoDetail>(`/api/videos/${id}/publish`, {
      method: "POST",
    }),
  unpublishVideo: (id: number) =>
    request<VideoDetail>(`/api/videos/${id}/unpublish`, {
      method: "POST",
    }),
  deleteVideo: (id: number) =>
    request<null>(`/api/videos/${id}`, {
      method: "DELETE",
    }),
  myVideos: (params: {
    page?: number;
    size?: number;
    keyword?: string;
    status?: VideoStatus;
    visibility?: VideoVisibility;
  }) => request<Page<MyVideoSummary>>("/api/me/videos", { query: params }),

  comments: (videoId: number, page = 0, size = 10) =>
    request<Page<CommentSummary>>("/api/comments", { query: { videoId, page, size } }),
  createComment: (payload: { videoId: number; content: string }) =>
    request<CommentDetail>("/api/comments", {
      method: "POST",
      body: jsonBody(payload),
    }),
  deleteComment: (commentId: number) =>
    request<null>("/api/comments", {
      method: "DELETE",
      query: { commentId },
    }),

  setReaction: (id: number, type: ReactionType) =>
    request<VideoEngagement>(`/api/videos/${id}/reaction`, {
      method: "PUT",
      body: jsonBody({ type }),
    }),
  clearReaction: (id: number) =>
    request<VideoEngagement>(`/api/videos/${id}/reaction`, {
      method: "DELETE",
    }),
  addFavorite: (id: number) =>
    request<VideoEngagement>(`/api/videos/${id}/favorite`, {
      method: "PUT",
    }),
  removeFavorite: (id: number) =>
    request<VideoEngagement>(`/api/videos/${id}/favorite`, {
      method: "DELETE",
    }),
  myFavorites: (page = 0, size = 10) =>
    request<Page<FavoriteVideoSummary>>("/api/me/favorites", { query: { page, size } }),

  subscribe: (username: string) =>
    request<AccountSubscription>(`/api/accounts/${encodeURIComponent(username)}/subscription`, {
      method: "PUT",
    }),
  unsubscribe: (username: string) =>
    request<AccountSubscription>(`/api/accounts/${encodeURIComponent(username)}/subscription`, {
      method: "DELETE",
    }),
};
