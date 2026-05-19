import axios from "axios";

const http = axios.create({
  baseURL: "/",
  timeout: 10000
});

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("admin_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/** 将可能是 JSON 字符串的响应体规范为对象，便于取 msg */
function normalizeBody(data: unknown): unknown {
  if (typeof data !== "string") return data;
  const s = data.trim();
  if (!s.startsWith("{") && !s.startsWith("[")) return data;
  try {
    return JSON.parse(s) as unknown;
  } catch {
    return data;
  }
}

function pickServerMsg(data: unknown): string | undefined {
  const normalized = normalizeBody(data);
  if (!normalized || typeof normalized !== "object") return undefined;
  const o = normalized as Record<string, unknown>;
  for (const key of ["msg", "message", "error_description"] as const) {
    const v = o[key];
    if (typeof v === "string") {
      const t = v.trim();
      if (t) return t;
    }
  }
  return undefined;
}

function isBizFailureEnvelope(body: unknown): body is { code: unknown; msg?: unknown } {
  if (!body || typeof body !== "object") return false;
  return "code" in body && (body as { code: unknown }).code !== undefined;
}

function shouldRejectBizCode(code: unknown): boolean {
  return code !== "0" && code !== 0;
}

http.interceptors.response.use(
  (resp) => {
    const body = normalizeBody(resp?.data);
    if (isBizFailureEnvelope(body) && shouldRejectBizCode(body.code)) {
      const msg = pickServerMsg(body) || "请求失败";
      return Promise.reject(new Error(msg));
    }
    return resp;
  },
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem("admin_token");
      location.href = "/admin/login";
    }
    const raw = error?.response?.data;
    const serverMsg = pickServerMsg(raw);
    if (serverMsg) {
      return Promise.reject(new Error(serverMsg));
    }
    return Promise.reject(error);
  }
);

export default http;
