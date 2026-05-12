import { AlertCircle, Loader2 } from "lucide-react";

export function LoadingState({ label = "加载中" }: { label?: string }) {
  return (
    <div className="state-view">
      <Loader2 className="spin" size={24} />
      <span>{label}</span>
    </div>
  );
}

export function EmptyState({ title, text }: { title: string; text?: string }) {
  return (
    <div className="state-view empty">
      <strong>{title}</strong>
      {text && <span>{text}</span>}
    </div>
  );
}

export function ErrorState({ message }: { message: string }) {
  return (
    <div className="state-view error">
      <AlertCircle size={22} />
      <span>{message}</span>
    </div>
  );
}
