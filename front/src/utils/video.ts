function isHttpUrl(value: string) {
  try {
    const url = new URL(value);
    return url.protocol === "http:" || url.protocol === "https:";
  } catch {
    return false;
  }
}

function toYouTubeEmbedUrl(url: URL) {
  const hostname = url.hostname.replace(/^www\./, "").toLowerCase();

  if (hostname === "youtu.be") {
    const videoId = url.pathname.slice(1);
    return videoId ? `https://www.youtube.com/embed/${videoId}` : null;
  }

  if (hostname === "youtube.com" || hostname === "m.youtube.com") {
    if (url.pathname === "/watch") {
      const videoId = url.searchParams.get("v");
      return videoId ? `https://www.youtube.com/embed/${videoId}` : null;
    }

    const embedMatch = url.pathname.match(/^\/embed\/([^/]+)/);
    if (embedMatch) {
      return `https://www.youtube.com/embed/${embedMatch[1]}`;
    }
  }

  return null;
}

function toBilibiliEmbedUrl(url: URL) {
  const hostname = url.hostname.replace(/^www\./, "").toLowerCase();

  if (hostname === "b23.tv") {
    return null;
  }

  if (hostname === "player.bilibili.com") {
    if (url.pathname === "/player.html") {
      return url.toString();
    }
    return null;
  }

  if (hostname === "bilibili.com" || hostname === "m.bilibili.com") {
    const bvMatch = url.pathname.match(/\/video\/(BV[0-9A-Za-z]+)/);
    if (bvMatch) {
      return `https://player.bilibili.com/player.html?bvid=${bvMatch[1]}&page=1`;
    }

    const avMatch = url.pathname.match(/\/video\/av(\d+)/i);
    if (avMatch) {
      return `https://player.bilibili.com/player.html?aid=${avMatch[1]}&page=1`;
    }
  }

  return null;
}

export function toPlatformEmbedUrl(value: string) {
  if (!isHttpUrl(value)) {
    return null;
  }

  const url = new URL(value);
  return toYouTubeEmbedUrl(url) ?? toBilibiliEmbedUrl(url);
}
