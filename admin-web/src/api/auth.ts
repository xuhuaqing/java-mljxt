import http from "./http";

export type LoginPayload = { account: string; password: string };

export async function login(payload: LoginPayload) {
  const { data } = await http.post("/api/auth/login", payload);
  if (data.code !== "0") throw new Error(data.msg || "登录失败");
  return data.data;
}

export async function fetchMe() {
  const { data } = await http.get("/api/admin/me");
  if (data.code !== "0") throw new Error(data.msg || "获取用户失败");
  return data.data;
}

export async function fetchMenus() {
  const { data } = await http.get("/api/admin/menus");
  if (data.code !== "0") throw new Error(data.msg || "获取菜单失败");
  return data.data as Array<{ path: string; name: string }>;
}

export async function fetchUsers(params: { role?: number; keyword?: string; pageNo: number; pageSize: number }) {
  const { data } = await http.get("/api/admin/users", { params });
  if (data.code !== "0") throw new Error(data.msg || "获取用户失败");
  return data.data as {
    total: number;
    pageNo: number;
    pageSize: number;
    records: Array<{ id: number; name: string; phone: string; password: string; role: number; roleName: string; status: number; remainingUseCount: number | null }>;
  };
}

export type AdminUserForm = {
  name: string;
  phone: string;
  password: string;
  role: 1 | 2 | 3 | 4;
  remainingUseCount?: number | null;
};

export async function createUser(payload: AdminUserForm) {
  const { data } = await http.post("/api/admin/users", payload);
  if (data.code !== "0") throw new Error(data.msg || "新增失败");
  return data.data;
}

export async function updateUser(id: number, payload: AdminUserForm) {
  const { data } = await http.put(`/api/admin/users/${id}`, payload);
  if (data.code !== "0") throw new Error(data.msg || "编辑失败");
  return data.data;
}

export async function deleteUser(id: number) {
  const { data } = await http.put(`/api/admin/users/${id}/disable`);
  if (data.code !== "0") throw new Error(data.msg || "停用失败");
}

export async function enableUser(id: number) {
  const { data } = await http.put(`/api/admin/users/${id}/enable`);
  if (data.code !== "0") throw new Error(data.msg || "启用失败");
}

export async function fetchMerchantOptions(keyword?: string) {
  const { data } = await http.get("/api/merchant/options", { params: { keyword } });
  if (data.code !== "0") throw new Error(data.msg || "获取商家列表失败");
  return data.data as Array<{ id: number; name: string }>;
}

export async function fetchDevices(params: { merchantId?: number; keyword?: string; pageNo: number; pageSize: number }) {
  const { data } = await http.get("/api/admin/devices", { params });
  if (data.code !== "0") throw new Error(data.msg || "获取设备失败");
  return data.data as {
    total: number;
    pageNo: number;
    pageSize: number;
    records: Array<{
      id: number; machineNo: string; deviceName: string; status: number; merchantId: number | null; merchantName: string | null; freeUseDeadline: string | null; inUse: boolean;
    }>;
  };
}

export async function createDevice(payload: { machineNo: string; deviceName: string; merchantId: number; freeUseDeadline?: string | null }) {
  const { data } = await http.post("/api/admin/devices", payload);
  if (data.code !== "0") throw new Error(data.msg || "新增设备失败");
}

export async function updateDevice(
  id: number,
  payload: { machineNo: string; deviceName: string; merchantId?: number | null; freeUseDeadline?: string | null }
) {
  const { data } = await http.put(`/api/admin/devices/${id}`, payload);
  if (data.code !== "0") throw new Error(data.msg || "编辑设备失败");
}

export async function updateDeviceDeadline(id: number, freeUseDeadline: string) {
  const { data } = await http.put(`/api/admin/devices/${id}/free-use-deadline`, { freeUseDeadline });
  if (data.code !== "0") throw new Error(data.msg || "设置截止时间失败");
}

export async function releaseDeviceUsage(id: number) {
  const { data } = await http.put(`/api/device/${id}/manual-refresh`);
  if (data.code !== "0") throw new Error(data.msg || "手动刷新失败");
}

export async function disableDevice(id: number) {
  const { data } = await http.put(`/api/admin/devices/${id}/disable`);
  if (data.code !== "0") throw new Error(data.msg || "停用设备失败");
}

export async function enableDevice(id: number) {
  const { data } = await http.put(`/api/admin/devices/${id}/enable`);
  if (data.code !== "0") throw new Error(data.msg || "启用设备失败");
}

export async function unbindDeviceMerchant(id: number) {
  const { data } = await http.put(`/api/admin/devices/${id}/unbind-merchant`);
  if (data.code !== "0") throw new Error(data.msg || "解绑商家失败");
}

export async function bindDeviceMerchant(id: number, merchantId: number) {
  const { data } = await http.put(`/api/admin/devices/${id}/merchant`, { merchantId });
  if (data.code !== "0") throw new Error(data.msg || "绑定商家失败");
}

export async function fetchDeviceOptionsByMerchant(merchantId?: number) {
  if (!merchantId) return [] as Array<{ id: number; deviceName: string; machineNo: string }>;
  const { data } = await http.get("/api/device/list-by-merchant", { params: { merchantId } });
  if (data.code !== "0") throw new Error(data.msg || "获取设备选项失败");
  return (data.data || []) as Array<{ id: number; deviceName: string; machineNo: string }>;
}

export async function fetchDeviceUsageRecords(params: { merchantId?: number; deviceId?: number; pageNo: number; pageSize: number }) {
  const { data } = await http.get("/api/admin/device-usage-records", { params });
  if (data.code !== "0") throw new Error(data.msg || "获取设备使用记录失败");
  return data.data as {
    total: number;
    pageNo: number;
    pageSize: number;
    records: Array<{
      orderId: number; merchantId: number; merchantName: string; deviceId: number; deviceName: string; userId: number; userName: string; userPhone: string;
      projectName: string; usageCount: number; createdAt: string;
    }>;
  };
}

export function exportDeviceUsageRecords(params: { merchantId?: number; deviceId?: number }) {
  const query = new URLSearchParams();
  if (params.merchantId) query.set("merchantId", String(params.merchantId));
  if (params.deviceId) query.set("deviceId", String(params.deviceId));
  const token = localStorage.getItem("admin_token");
  const url = `/api/admin/device-usage-records/export${query.toString() ? `?${query}` : ""}`;
  return fetch(url, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
    .then(async (r) => {
      if (!r.ok) {
        const text = await r.text();
        let msg = "导出失败";
        try {
          const j = JSON.parse(text) as { msg?: unknown };
          if (typeof j?.msg === "string" && j.msg.trim()) msg = j.msg.trim();
        } catch {
          /* 非 JSON 错误体则沿用默认文案 */
        }
        throw new Error(msg);
      }
      const blob = await r.blob();
      const objUrl = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = objUrl;
      a.download = "device-usage-records.xls";
      a.click();
      window.URL.revokeObjectURL(objUrl);
    });
}

export async function bindDeveloperMerchant(payload: { developerId: number; merchantId: number }) {
  const { data } = await http.post("/api/developer-merchant/bind", payload);
  if (data.code !== "0") throw new Error(data.msg || "绑定失败");
  return data.data as { developerId: number; merchantId: number; alreadyBound: boolean };
}

export async function unbindDeveloperMerchant(payload: { developerId: number; merchantId: number }) {
  const { data } = await http.post("/api/developer-merchant/unbind", payload);
  if (data.code !== "0") throw new Error(data.msg || "解绑失败");
  return data.data as { developerId: number; merchantId: number; alreadyBound: boolean };
}

export async function fetchOrderRecords(params: {
  merchantId?: number;
  deviceId?: number;
  phone?: string;
  pageNo: number;
  pageSize: number;
}) {
  const { data } = await http.get("/api/admin/order-records", { params });
  if (data.code !== "0") throw new Error(data.msg || "获取下单记录失败");
  return data.data as {
    total: number;
    pageNo: number;
    pageSize: number;
    records: Array<{
      orderId: number;
      userId: number;
      userName: string;
      userPhone: string;
      merchantId: number;
      merchantName: string;
      deviceId: number;
      deviceName: string;
      projectName: string;
      projectDuration: number;
      usageCount: number;
      createdAt: string;
    }>;
  };
}

export async function fetchWithdrawRecords(params: {
  developerId?: number;
  pageNo: number;
  pageSize: number;
}) {
  const { data } = await http.get("/api/admin/withdraw-records", { params });
  if (data.code !== "0") throw new Error(data.msg || "获取提现明细失败");
  return data.data as {
    total: number;
    pageNo: number;
    pageSize: number;
    records: Array<{
      withdrawRecordId: number;
      developerId: number;
      developerName: string;
      developerPhone: string;
      usageCountSnapshot: number;
      createdAt: string;
    }>;
  };
}

export async function fetchDeveloperBoundList(developerId: number) {
  const { data } = await http.get("/api/developer-merchant/bound-list", { params: { developerId } });
  if (data.code !== "0") throw new Error(data.msg || "查询绑定列表失败");
  return (data.data || []) as Array<{
    bindId: number;
    developerId: number;
    merchantId: number;
    merchantName: string;
    merchantPhone: string;
    remainingUseCount: number;
    merchantTotalDeviceUsageCount: number;
    deviceId: number;
    machineNo: string;
    deviceName: string;
    deviceStatus: number;
    freeUseDeadline: string | null;
    bindTime: string;
  }>;
}
