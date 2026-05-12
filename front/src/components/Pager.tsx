import { ChevronLeft, ChevronRight } from "lucide-react";
import type { Page } from "../api/types";

export function Pager<T>({
  page,
  onPage,
}: {
  page: Page<T> | null;
  onPage: (next: number) => void;
}) {
  if (!page || page.totalPages <= 1) return null;

  return (
    <div className="pager">
      <button type="button" disabled={page.first} onClick={() => onPage(page.number - 1)}>
        <ChevronLeft size={17} /> 上一页
      </button>
      <span>
        {page.number + 1} / {page.totalPages}
      </span>
      <button type="button" disabled={page.last} onClick={() => onPage(page.number + 1)}>
        下一页 <ChevronRight size={17} />
      </button>
    </div>
  );
}
