import type { ApiResponse } from "./types";

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim();

export const API_BASE_URL = configuredBaseUrl ? configuredBaseUrl.replace(/\/$/, "") : "";

export class ApiError extends Error {
  status: number;

  constructor(message: string, status: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

type JsonRecord = Record<string, unknown>;

function toQuery(params: JsonRecord = {}) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      query.set(key, String(value));
    }
  });
  const text = query.toString();
  return text ? `?${text}` : "";
}

async function parseResponse<T>(response: Response, expectJson: boolean): Promise<T> {
  const contentType = response.headers.get("content-type") ?? "";
  const isJson = contentType.includes("application/json");
  const payload = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    const message =
      isJson && payload && typeof payload.message === "string"
        ? payload.message
        : response.status === 401
          ? "请先登录后再继续"
          : "请求失败";
    throw new ApiError(message, response.status);
  }

  if (expectJson && !isJson) {
    throw new ApiError("接口未返回 JSON。请确认 VITE_API_BASE_URL 指向正在运行的后端服务。", response.status);
  }

  if (isJson && payload && typeof payload === "object" && "success" in payload) {
    const apiPayload = payload as ApiResponse<T>;
    if (!apiPayload.success) {
      throw new ApiError(apiPayload.message || "请求失败", response.status);
    }
    return apiPayload.data;
  }

  return payload as T;
}

export async function request<T>(
  path: string,
  options: RequestInit & { expectJson?: boolean; query?: JsonRecord } = {},
): Promise<T> {
  const { query, headers, body, expectJson = true, ...rest } = options;
  const response = await fetch(`${API_BASE_URL}${path}${toQuery(query)}`, {
    credentials: "include",
    headers: {
      ...(body ? { "Content-Type": "application/json" } : {}),
      ...headers,
    },
    body,
    ...rest,
  });

  return parseResponse<T>(response, expectJson);
}

export function jsonBody(payload: unknown) {
  return JSON.stringify(payload);
}
