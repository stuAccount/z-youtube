import { SlidersHorizontal } from "lucide-react";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { api } from "../api/endpoints";
import type { Page, PublicVideoSummary } from "../api/types";
import { EmptyState, ErrorState, LoadingState } from "../components/StateViews";
import { VideoCard } from "../components/VideoCard";
import { Pager } from "../components/Pager";
import { errorMessage } from "../utils/format";

export function HomePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get("q") ?? "";
  const authorId = searchParams.get("authorId") ?? "";
  const pageNumber = Number(searchParams.get("page") ?? 0);
  const [page, setPage] = useState<Page<PublicVideoSummary> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");
    const loader = keyword || authorId ? api.videos : (params: { page?: number; size?: number }) => api.latestFeed(params.page, params.size);
    loader({
      keyword,
      authorId: authorId ? Number(authorId) : undefined,
      page: Number.isFinite(pageNumber) ? pageNumber : 0,
      size: 12,
      status: "PUBLISHED",
      visibility: "PUBLIC",
    })
      .then(setPage)
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false));
  }, [keyword, authorId, pageNumber]);

  const goPage = (next: number) => {
    const params = new URLSearchParams(searchParams);
    params.set("page", String(next));
    setSearchParams(params);
  };

  return (
    <section className="page-stack">
      <div className="section-head">
        <div>
          <p className="eyebrow">公开视频流</p>
          <h1>{keyword ? `搜索：${keyword}` : "最新公开视频"}</h1>
        </div>
        <div className="filter-note">
          <SlidersHorizontal size={17} />
          仅展示 PUBLISHED / PUBLIC
        </div>
      </div>

      {loading && <LoadingState label="正在读取视频列表" />}
      {error && <ErrorState message={error} />}
      {!loading && !error && page?.content.length === 0 && (
        <EmptyState title="还没有公开视频" text="服务器有内容后，这里会自动出现视频卡片。" />
      )}
      {!loading && !error && page && page.content.length > 0 && (
        <>
          <div className="video-grid">
            {page.content.map((video) => (
              <VideoCard key={video.id} video={video} />
            ))}
          </div>
          <Pager page={page} onPage={goPage} />
        </>
      )}
    </section>
  );
}
