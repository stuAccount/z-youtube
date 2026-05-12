import { Eye, Lock, Play, Radio } from "lucide-react";
import { Link } from "react-router-dom";
import type { FavoriteVideoSummary, MyVideoSummary, PublicVideoSummary } from "../api/types";
import { formatDate, initials } from "../utils/format";

type VideoLike = PublicVideoSummary | MyVideoSummary | FavoriteVideoSummary;

export function VideoCard({ video, compact = false }: { video: VideoLike; compact?: boolean }) {
  const author = "author" in video ? video.author : null;
  const coverUrl = "coverUrl" in video ? video.coverUrl : null;

  return (
    <article className={`video-card ${compact ? "compact" : ""}`}>
      <Link className="thumbnail" to={`/videos/${video.id}`}>
        {coverUrl ? <img src={coverUrl} alt="" loading="lazy" /> : <div className="thumbnail-fallback">Z</div>}
        <span className="play-chip">
          <Play size={15} fill="currentColor" />
        </span>
      </Link>
      <div className="video-meta">
        {author && (
          <Link className="mini-avatar" to={`/channel/${author.username}`}>
            {initials(author.nickname || author.username)}
          </Link>
        )}
        <div>
          <Link className="video-title" to={`/videos/${video.id}`}>
            {video.title}
          </Link>
          {author && (
            <Link className="muted-link" to={`/channel/${author.username}`}>
              {author.nickname || author.username}
            </Link>
          )}
          <div className="meta-line">
            <span>{formatDate(video.createdAt)}</span>
            <span className="dot" />
            <span className="status-mini">
              {video.status === "PUBLISHED" ? <Radio size={13} /> : <Lock size={13} />}
              {video.status}
            </span>
            <span className="dot" />
            <span>
              <Eye size={13} /> {video.visibility}
            </span>
          </div>
        </div>
      </div>
    </article>
  );
}
