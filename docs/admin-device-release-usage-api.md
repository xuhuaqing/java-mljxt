# 管理后台 - 设备手动释放占用（历史文档）

> **推荐**：前端「手动刷新」请使用无需 Token 的新接口，见 [device-manual-refresh-api.md](./device-manual-refresh-api.md)  
> `PUT /api/device/{id}/manual-refresh`

以下为原管理端接口（需管理员 Token）：

## 基本信息

- 接口名称：手动释放设备使用占用
- 请求方式：`PUT`
- 请求路径：`/api/admin/devices/{id}/release-usage`
- 权限：**仅后台管理员**（`user_account.role = 0`）
- 完整示例：`PUT http://localhost:8877/api/admin/devices/101/release-usage`

## 业务说明

设备在用户通过 MQTT 下发项目后会写入 `usage_record`，在 `project_duration`（默认 40 分钟）内视为「使用中」，同一设备不可再次下单/发令。

若设备异常导致占用未自动结束，管理员可调用本接口，将当前进行中的使用记录标记为已释放（`released_at`），释放后用户可立即重新下单。

配合设备列表接口 `GET /api/admin/devices` 返回的 `inUse` 字段，可在「使用中」时展示「手动刷新」按钮。

**数据库前置**：需先执行 `scripts/mysql-usage-record-released-at.sql` 增加 `usage_record.released_at` 字段。

## 请求头

| 字段 | 必填 | 说明 |
|---|---|---|
| Authorization | 是 | `Bearer <admin_token>`，管理员登录后获得 |

## 路径参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | number | 是 | 设备ID（`merchant_device.id`） |

无请求体。

请求示例：

```http
PUT /api/admin/devices/101/release-usage
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 返回结构

成功：

```json
{
  "code": "0",
  "msg": "success",
  "data": null
}
```

## 失败响应示例

### 1) 未登录或非管理员

```json
{
  "code": "400",
  "msg": "仅后台管理员可访问",
  "data": null
}
```

### 2) 设备不存在

```json
{
  "code": "400",
  "msg": "设备不存在",
  "data": null
}
```

### 3) 当前未在使用中

```json
{
  "code": "400",
  "msg": "设备当前未在使用中，无需刷新",
  "data": null
}
```

## 设备列表中的 inUse 字段

`GET /api/admin/devices` 每条记录增加：

| 字段 | 类型 | 说明 |
|---|---|---|
| inUse | boolean | `true` 表示设备占用中，可展示「手动刷新」；`false` 表示空闲 |

## 前端调用示例

### 方式一：管理后台工程（已封装）

文件：`admin-web/src/api/auth.ts`

```ts
import { releaseDeviceUsage } from "../api/auth";

// 设备 ID
await releaseDeviceUsage(101);
```

页面：`admin-web/src/views/DevicesView.vue` 操作列「手动刷新」已接入。

### 方式二：fetch（任意前端）

```ts
const API_BASE = "http://localhost:8877";

export async function releaseDeviceUsage(deviceId: number, adminToken: string) {
  const res = await fetch(`${API_BASE}/api/admin/devices/${deviceId}/release-usage`, {
    method: "PUT",
    headers: {
      Authorization: `Bearer ${adminToken}`,
    },
  });
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "手动刷新失败");
  }
}
```

### 方式三：axios

```ts
import axios from "axios";

const http = axios.create({ baseURL: "http://localhost:8877" });

http.interceptors.request.use((config) => {
  const token = localStorage.getItem("admin_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export async function releaseDeviceUsage(deviceId: number) {
  const { data } = await http.put(`/api/admin/devices/${deviceId}/release-usage`);
  if (data.code !== "0") throw new Error(data.msg || "手动刷新失败");
}
```

## 相关接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/admin/devices` | 设备列表（含 `inUse`） |
| PUT | `/api/admin/devices/{id}/release-usage` | 本接口：手动释放占用 |
| POST | `/api/auth/login` | 管理员登录获取 token |
