export function formatDate(value: string | null | undefined) {
  if (!value) return "未知时间";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat("zh-CN", {
    month: "short",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}

export function initials(name: string | null | undefined) {
  return (name || "Z").trim().slice(0, 1).toUpperCase() || "Z";
}

export function compactNumber(value: number) {
  return new Intl.NumberFormat("zh-CN", { notation: "compact" }).format(value);
}

export function errorMessage(error: unknown) {
  return error instanceof Error ? error.message : "操作失败";
}
