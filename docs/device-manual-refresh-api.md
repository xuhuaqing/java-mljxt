# 设备手动刷新接口（释放占用，允许再次下单）

## 基本信息

- 接口名称：设备手动刷新
- 请求方式：`PUT`
- 请求路径：`/api/device/{id}/manual-refresh`
- 权限：**无需 Token**，前端可直接调用
- 完整示例：`PUT http://localhost:8877/api/device/101/manual-refresh`

## 业务说明

设备在用户通过 MQTT 下发项目后会写入 `usage_record`，在 `project_duration`（默认 40 分钟）内视为「使用中」，同一设备不可再次下单/发令。

若设备异常导致占用未自动结束，可调用本接口释放当前占用（写入 `released_at`），释放后用户可立即重新下单。

**数据库前置**：需先执行 `scripts/mysql-usage-record-released-at.sql` 增加 `usage_record.released_at` 字段。

## 路径参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| id | number | 是 | 设备ID（`merchant_device.id`） |

无请求体，无需请求头。

请求示例：

```http
PUT /api/device/101/manual-refresh
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

### 1) 设备不存在

```json
{
  "code": "400",
  "msg": "设备不存在",
  "data": null
}
```

### 2) 当前未在使用中

```json
{
  "code": "400",
  "msg": "设备当前未在使用中，无需刷新",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export async function manualRefreshDevice(deviceId: number) {
  const res = await fetch(`${API_BASE}/api/device/${deviceId}/manual-refresh`, {
    method: "PUT",
  });
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "手动刷新失败");
  }
}
```

管理后台页面已封装：`admin-web/src/api/auth.ts` 中的 `releaseDeviceUsage(id)`。

## 说明

- 原管理端路径 `PUT /api/admin/devices/{id}/release-usage` 仍保留，需管理员 Token；新业务前端请使用本接口。
