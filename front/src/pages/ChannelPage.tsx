import { Bell, BellOff } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { api } from "../api/endpoints";
import type { Page, PublicProfile, PublicVideoSummary } from "../api/types";
import { EmptyState, ErrorState, LoadingState } from "../components/StateViews";
import { VideoCard } from "../components/VideoCard";
import { useAuth } from "../context/AuthContext";
import { compactNumber, errorMessage, initials } from "../utils/format";

export function ChannelPage() {
  const { username = "" } = useParams();
  const [profile, setProfile] = useState<PublicProfile | null>(null);
  const [videos, setVideos] = useState<Page<PublicVideoSummary> | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();
  const navigate = useNavigate();

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const loadedProfile = await api.publicProfile(username);
      setProfile(loadedProfile);
      setVideos(
        await api.videos({
          authorId: loadedProfile.id,
          page: 0,
          size: 12,
          status: "PUBLISHED",
          visibility: "PUBLIC",
        }),
      );
    } catch (err) {
      setError(errorMessage(err));
    } finally {
      setLoading(false);
    }
  }, [username]);

  useEffect(() => {
    load();
  }, [load]);

  const toggleSubscription = async () => {
    if (!profile) return;
    if (!user) {
      navigate("/auth");
      return;
    }
    try {
      const result = profile.subscribedByCurrentUser
        ? await api.unsubscribe(profile.username)
        : await api.subscribe(profile.username);
      setProfile({
        ...profile,
        subscribedByCurrentUser: result.subscribed,
        subscriberCount: result.subscriberCount,
      });
    } catch (err) {
      setError(errorMessage(err));
    }
  };

  if (loading) return <LoadingState label="正在打开频道" />;
  if (error) return <ErrorState message={error} />;
  if (!profile) return <EmptyState title="频道不存在" />;

  const isSelf = user?.username === profile.username;

  return (
    <section className="page-stack">
      <div className="channel-hero">
        <div className="big-avatar">{initials(profile.nickname || profile.username)}</div>
        <div>
          <h1>{profile.nickname || profile.username}</h1>
          <p>@{profile.username}</p>
          <p>{profile.bio || "这个频道还没有简介。"}</p>
          <div className="stat-row">
            <span>{compactNumber(profile.subscriberCount)} 订阅者</span>
            <span>{compactNumber(profile.subscriptionCount)} 正在订阅</span>
          </div>
        </div>
        {!isSelf && (
          <button className={`subscribe-button ${profile.subscribedByCurrentUser ? "subscribed" : ""}`} onClick={toggleSubscription}>
            {profile.subscribedByCurrentUser ? <BellOff size={17} /> : <Bell size={17} />}
            {profile.subscribedByCurrentUser ? "已订阅" : "订阅"}
          </button>
        )}
      </div>

      <div className="section-head">
        <div>
          <p className="eyebrow">频道内容</p>
          <h2>公开视频</h2>
        </div>
      </div>
      {videos?.content.length ? (
        <div className="video-grid">
          {videos.content.map((video) => (
            <VideoCard key={video.id} video={video} />
          ))}
        </div>
      ) : (
        <EmptyState title="这个频道还没有公开视频" />
      )}
    </section>
  );
}
