import { Edit3, Plus, Radio, Save, Trash2 } from "lucide-react";
import { FormEvent, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/endpoints";
import type { MyVideoSummary, Page, UpdateVideoPayload, VideoVisibility } from "../api/types";
import { EmptyState, ErrorState, LoadingState } from "../components/StateViews";
import { useAuth } from "../context/AuthContext";
import { errorMessage, formatDate } from "../utils/format";

const emptyDraft = {
  title: "",
  description: "",
  videoUrl: "",
  coverUrl: "",
  visibility: "PRIVATE" as VideoVisibility,
};

export function StudioPage() {
  const { user, loading: authLoading } = useAuth();
  const navigate = useNavigate();
  const [videos, setVideos] = useState<Page<MyVideoSummary> | null>(null);
  const [draft, setDraft] = useState(emptyDraft);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editDraft, setEditDraft] = useState<UpdateVideoPayload>({});
  const [keyword, setKeyword] = useState("");
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!authLoading && !user) navigate("/auth");
  }, [authLoading, user, navigate]);

  const load = () => {
    if (!user) return;
    setLoading(true);
    api
      .myVideos({ page: 0, size: 20, keyword })
      .then(setVideos)
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [user]);

  const editingVideo = useMemo(() => videos?.content.find((video) => video.id === editingId), [editingId, videos]);

  const createVideo = async (event: FormEvent) => {
    event.preventDefault();
    setMessage("");
    try {
      await api.createVideo({
        ...draft,
        coverUrl: draft.coverUrl || null,
      });
      setDraft(emptyDraft);
      setMessage("视频草稿已创建");
      load();
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const startEdit = (video: MyVideoSummary) => {
    setEditingId(video.id);
    setEditDraft({
      title: video.title,
      videoUrl: video.videoUrl,
      coverUrl: video.coverUrl ?? "",
      visibility: video.visibility,
    });
  };

  const saveEdit = async () => {
    if (!editingId) return;
    try {
      await api.updateVideo(editingId, {
        ...editDraft,
        coverUrl: editDraft.coverUrl || null,
      });
      setEditingId(null);
      setMessage("视频信息已保存");
      load();
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const publishToggle = async (video: MyVideoSummary) => {
    try {
      if (video.status === "PUBLISHED") {
        await api.unpublishVideo(video.id);
      } else {
        await api.publishVideo(video.id);
      }
      load();
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  const deleteVideo = async (video: MyVideoSummary) => {
    if (!confirm(`删除视频「${video.title}」？`)) return;
    try {
      await api.deleteVideo(video.id);
      load();
    } catch (err) {
      setMessage(errorMessage(err));
    }
  };

  if (authLoading || loading) return <LoadingState label="正在打开创作者管理" />;
  if (error) return <ErrorState message={error} />;

  return (
    <section className="studio-layout">
      <form className="studio-create" onSubmit={createVideo}>
        <p className="eyebrow">新建视频</p>
        <h1>发布前先保存为草稿</h1>
        <input placeholder="标题" value={draft.title} onChange={(event) => setDraft({ ...draft, title: event.target.value })} maxLength={100} required />
        <textarea
          placeholder="简介"
          value={draft.description}
          onChange={(event) => setDraft({ ...draft, description: event.target.value })}
          maxLength={5000}
          required
        />
        <input placeholder="视频 URL" value={draft.videoUrl} onChange={(event) => setDraft({ ...draft, videoUrl: event.target.value })} required />
        <input placeholder="封面 URL" value={draft.coverUrl} onChange={(event) => setDraft({ ...draft, coverUrl: event.target.value })} />
        <select value={draft.visibility} onChange={(event) => setDraft({ ...draft, visibility: event.target.value as VideoVisibility })}>
          <option value="PRIVATE">PRIVATE</option>
          <option value="UNLISTED">UNLISTED</option>
          <option value="PUBLIC">PUBLIC</option>
        </select>
        <button className="primary-button" type="submit">
          <Plus size={17} /> 创建草稿
        </button>
        {message && <p className="form-error neutral">{message}</p>}
      </form>

      <div className="studio-list">
        <div className="section-head">
          <div>
            <p className="eyebrow">我的视频</p>
            <h2>内容管理</h2>
          </div>
          <form
            className="inline-search"
            onSubmit={(event) => {
              event.preventDefault();
              load();
            }}
          >
            <input value={keyword} onChange={(event) => setKeyword(event.target.value)} placeholder="筛选标题" />
          </form>
        </div>

        {videos?.content.length ? (
          <div className="table-list">
            {videos.content.map((video) => (
              <article className="studio-row" key={video.id}>
                <Link className="studio-thumb" to={`/videos/${video.id}`}>
                  {video.coverUrl ? <img src={video.coverUrl} alt="" /> : <span>Z</span>}
                </Link>
                <div className="studio-main">
                  {editingId === video.id ? (
                    <div className="edit-grid">
                      <input value={editDraft.title ?? ""} onChange={(event) => setEditDraft({ ...editDraft, title: event.target.value })} />
                      <input value={editDraft.videoUrl ?? ""} onChange={(event) => setEditDraft({ ...editDraft, videoUrl: event.target.value })} />
                      <input value={editDraft.coverUrl ?? ""} onChange={(event) => setEditDraft({ ...editDraft, coverUrl: event.target.value })} />
                      <select
                        value={editDraft.visibility ?? video.visibility}
                        onChange={(event) => setEditDraft({ ...editDraft, visibility: event.target.value as VideoVisibility })}
                      >
                        <option value="PRIVATE">PRIVATE</option>
                        <option value="UNLISTED">UNLISTED</option>
                        <option value="PUBLIC">PUBLIC</option>
                      </select>
                    </div>
                  ) : (
                    <>
                      <Link to={`/videos/${video.id}`}>{video.title}</Link>
                      <p>
                        {video.status} / {video.visibility} / 更新于 {formatDate(video.updatedAt)}
                      </p>
                    </>
                  )}
                </div>
                <div className="row-actions">
                  {editingId === video.id ? (
                    <button type="button" onClick={saveEdit} title="保存">
                      <Save size={17} />
                    </button>
                  ) : (
                    <button type="button" onClick={() => startEdit(video)} title="编辑">
                      <Edit3 size={17} />
                    </button>
                  )}
                  <button type="button" onClick={() => publishToggle(video)} title={video.status === "PUBLISHED" ? "下架" : "发布"}>
                    <Radio size={17} />
                  </button>
                  <button type="button" onClick={() => deleteVideo(video)} title="删除">
                    <Trash2 size={17} />
                  </button>
                </div>
              </article>
            ))}
          </div>
        ) : (
          <EmptyState title="还没有视频" text="创建草稿后会出现在这里。" />
        )}
      </div>
    </section>
  );
}
