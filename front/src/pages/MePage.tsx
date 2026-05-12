import { Heart, Save, ShieldAlert } from "lucide-react";
import { FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/endpoints";
import type { FavoriteVideoSummary, Page, SelfProfile } from "../api/types";
import { EmptyState, ErrorState, LoadingState } from "../components/StateViews";
import { VideoCard } from "../components/VideoCard";
import { useAuth } from "../context/AuthContext";
import { compactNumber, errorMessage, initials } from "../utils/format";

export function MePage() {
  const { user, loading: authLoading, setUser } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState<SelfProfile | null>(null);
  const [favorites, setFavorites] = useState<Page<FavoriteVideoSummary> | null>(null);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [passwords, setPasswords] = useState({ oldPassword: "", newPassword: "", confirmPassword: "" });

  useEffect(() => {
    if (!authLoading && !user) navigate("/auth");
  }, [authLoading, user, navigate]);

  useEffect(() => {
    if (!user) return;
    Promise.all([api.selfProfile(), api.myFavorites()])
      .then(([loadedProfile, loadedFavorites]) => {
        setProfile(loadedProfile);
        setFavorites(loadedFavorites);
      })
      .catch((err) => setError(errorMessage(err)));
  }, [user]);

  const updateProfile = async (event: FormEvent) => {
    event.preventDefault();
    if (!profile) return;
    setMessage("");
    try {
      const updated = await api.updateProfile({
        email: profile.email,
        nickname: profile.nickname,
        avatarUrl: profile.avatarUrl,
        bio: profile.bio,
      });
      setProfile(updated);
      setUser(updated);
      setMessage("资料已保存");
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const changePassword = async (event: FormEvent) => {
    event.preventDefault();
    setMessage("");
    try {
      await api.changePassword(passwords);
      setPasswords({ oldPassword: "", newPassword: "", confirmPassword: "" });
      setMessage("密码已更新");
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const withdraw = async () => {
    if (!confirm("确认注销当前账号？")) return;
    try {
      await api.withdraw();
      setUser(null);
      navigate("/auth");
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  if (authLoading || (!profile && !error)) return <LoadingState label="正在读取我的空间" />;
  if (error) return <ErrorState message={error} />;
  if (!profile) return null;

  return (
    <section className="me-layout">
      <div className="profile-panel">
        <div className="profile-top">
          <div className="big-avatar">{initials(profile.nickname || profile.username)}</div>
          <div>
            <h1>{profile.nickname || profile.username}</h1>
            <p>@{profile.username}</p>
            <div className="stat-row">
              <span>{compactNumber(profile.subscriberCount)} 订阅者</span>
              <span>{compactNumber(profile.subscriptionCount)} 正在订阅</span>
            </div>
          </div>
        </div>

        <form className="form-grid" onSubmit={updateProfile}>
          <label>
            邮箱
            <input value={profile.email} onChange={(event) => setProfile({ ...profile, email: event.target.value })} />
          </label>
          <label>
            昵称
            <input value={profile.nickname ?? ""} onChange={(event) => setProfile({ ...profile, nickname: event.target.value })} maxLength={50} />
          </label>
          <label>
            头像 URL
            <input value={profile.avatarUrl ?? ""} onChange={(event) => setProfile({ ...profile, avatarUrl: event.target.value })} />
          </label>
          <label>
            简介
            <textarea value={profile.bio ?? ""} onChange={(event) => setProfile({ ...profile, bio: event.target.value })} maxLength={500} />
          </label>
          <button className="primary-button" type="submit">
            <Save size={16} /> 保存资料
          </button>
        </form>

        <form className="form-grid password-form" onSubmit={changePassword}>
          <h2>修改密码</h2>
          <input
            type="password"
            placeholder="旧密码"
            value={passwords.oldPassword}
            onChange={(event) => setPasswords({ ...passwords, oldPassword: event.target.value })}
          />
          <input
            type="password"
            placeholder="新密码"
            value={passwords.newPassword}
            onChange={(event) => setPasswords({ ...passwords, newPassword: event.target.value })}
          />
          <input
            type="password"
            placeholder="确认新密码"
            value={passwords.confirmPassword}
            onChange={(event) => setPasswords({ ...passwords, confirmPassword: event.target.value })}
          />
          <button className="pill-button" type="submit">
            更新密码
          </button>
        </form>

        {message && <p className="form-error neutral">{message}</p>}
        <button className="danger-button" type="button" onClick={withdraw}>
          <ShieldAlert size={16} /> 注销账号
        </button>
      </div>

      <div className="favorites-panel">
        <div className="section-head">
          <div>
            <p className="eyebrow">我的收藏</p>
            <h2>
              <Heart size={20} /> 收藏视频
            </h2>
          </div>
          <Link className="pill-button ghost" to="/studio">
            创作者管理
          </Link>
        </div>
        {favorites?.content.length ? (
          <div className="stack-list">
            {favorites.content.map((video) => (
              <VideoCard key={video.id} video={video} compact />
            ))}
          </div>
        ) : (
          <EmptyState title="还没有收藏视频" />
        )}
      </div>
    </section>
  );
}
