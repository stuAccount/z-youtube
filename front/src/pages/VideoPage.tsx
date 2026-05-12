import { Heart, MessageSquare, ThumbsDown, ThumbsUp, UserPlus } from "lucide-react";
import { FormEvent, useCallback, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { ApiError } from "../api/client";
import { api } from "../api/endpoints";
import type { CommentSummary, Page, VideoDetail } from "../api/types";
import { EmptyState, ErrorState, LoadingState } from "../components/StateViews";
import { useAuth } from "../context/AuthContext";
import { compactNumber, errorMessage, formatDate, initials } from "../utils/format";

export function VideoPage() {
  const { id } = useParams();
  const videoId = Number(id);
  const [video, setVideo] = useState<VideoDetail | null>(null);
  const [comments, setComments] = useState<Page<CommentSummary> | null>(null);
  const [commentText, setCommentText] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const navigate = useNavigate();

  const load = useCallback(async () => {
    if (!Number.isFinite(videoId)) return;
    setLoading(true);
    setError("");
    try {
      const detail = await api.videoDetail(videoId);
      setVideo(detail);
      api.recordView(videoId)
        .then((view) => setVideo((current) => (current ? { ...current, viewCount: view.viewCount } : current)))
        .catch(() => undefined);
      setComments(await api.comments(videoId));
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [videoId]);

  useEffect(() => {
    load();
  }, [load]);

  const requireLogin = () => {
    if (!user) {
      navigate("/auth");
      return false;
    }
    return true;
  };

  const updateEngagement = async (action: "LIKE" | "DISLIKE" | "CLEAR" | "FAVORITE" | "UNFAVORITE") => {
    if (!video || !requireLogin()) return;
    setMessage("");
    try {
      const engagement =
        action === "CLEAR"
          ? await api.clearReaction(video.id)
          : action === "FAVORITE"
            ? await api.addFavorite(video.id)
            : action === "UNFAVORITE"
              ? await api.removeFavorite(video.id)
              : await api.setReaction(video.id, action);
      setVideo({ ...video, ...engagement });
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) navigate("/auth");
      setMessage(errorMessage(err));
    }
  };

  const submitComment = async (event: FormEvent) => {
    event.preventDefault();
    if (!video || !requireLogin()) return;
    setMessage("");
    try {
      await api.createComment({ videoId: video.id, content: commentText });
      setCommentText("");
      setComments(await api.comments(video.id));
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const deleteComment = async (commentId: number) => {
    if (!video || !requireLogin()) return;
    try {
      await api.deleteComment(commentId);
      setComments(await api.comments(video.id));
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  if (loading) return <LoadingState label="正在打开视频" />;
  if (error) return <ErrorState message={error} />;
  if (!video) return <EmptyState title="没有找到视频" />;

  return (
    <section className="watch-layout">
      <div className="watch-main">
        <div className="player-shell">
          <video src={video.videoUrl} poster={video.coverUrl ?? undefined} controls playsInline />
        </div>
        <h1>{video.title}</h1>
        <div className="watch-stats">
          <span>{compactNumber(video.viewCount)} 次观看</span>
          <span>{formatDate(video.createdAt)}</span>
          <span>{video.status}</span>
          <span>{video.visibility}</span>
        </div>
        <div className="creator-row">
          <Link className="channel-avatar" to={`/channel/${video.author.username}`}>
            {initials(video.author.nickname || video.author.username)}
          </Link>
          <div>
            <Link className="creator-name" to={`/channel/${video.author.username}`}>
              {video.author.nickname || video.author.username}
            </Link>
            <p>@{video.author.username}</p>
          </div>
          <Link className="pill-button ghost" to={`/channel/${video.author.username}`}>
            <UserPlus size={16} /> 频道
          </Link>
        </div>
        <p className="description">{video.description}</p>
        <div className="watch-actions">
          <button
            className={video.myReaction === "LIKE" ? "active" : ""}
            onClick={() => updateEngagement(video.myReaction === "LIKE" ? "CLEAR" : "LIKE")}
          >
            <ThumbsUp size={18} /> {compactNumber(video.likeCount)}
          </button>
          <button
            className={video.myReaction === "DISLIKE" ? "active" : ""}
            onClick={() => updateEngagement(video.myReaction === "DISLIKE" ? "CLEAR" : "DISLIKE")}
          >
            <ThumbsDown size={18} /> {compactNumber(video.dislikeCount)}
          </button>
          <button className={video.favorited ? "active" : ""} onClick={() => updateEngagement(video.favorited ? "UNFAVORITE" : "FAVORITE")}>
            <Heart size={18} /> {compactNumber(video.favoriteCount)}
          </button>
          <a className="plain-link" href={video.videoUrl} target="_blank" rel="noreferrer">
            打开原视频链接
          </a>
        </div>
        {message && <p className="form-error">{message}</p>}
      </div>

      <aside className="comments-panel">
        <h2>
          <MessageSquare size={19} /> 评论
        </h2>
        <form className="comment-form" onSubmit={submitComment}>
          <textarea value={commentText} onChange={(event) => setCommentText(event.target.value)} placeholder="写一条评论" maxLength={1000} />
          <button className="primary-button" type="submit">
            发布
          </button>
        </form>
        <div className="comment-list">
          {comments?.content.length ? (
            comments.content.map((comment) => (
              <article className="comment" key={comment.id}>
                <div className="mini-avatar">{initials(comment.author.nickname || comment.author.username)}</div>
                <div>
                  <strong>{comment.author.nickname || comment.author.username}</strong>
                  <span>{formatDate(comment.createdAt)}</span>
                  <p>{comment.content}</p>
                  {user?.id === comment.author.id && (
                    <button className="text-button" type="button" onClick={() => deleteComment(comment.id)}>
                      删除
                    </button>
                  )}
                </div>
              </article>
            ))
          ) : (
            <EmptyState title="暂无评论" text="公开视频和非私密已发布视频可以评论。" />
          )}
        </div>
      </aside>
    </section>
  );
}
