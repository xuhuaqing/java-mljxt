# 开发绑定商家接口文档

## 基本信息

- 模块：开发与商家绑定
- 统一返回：`{"code":"","msg":"","data":{}}`
- 角色约束：
  - 开发：`role=4`
  - 商家：`role=3`

---

## 1) 开发绑定商家

### 接口信息

- 接口名称：开发绑定商家
- 请求方式：`POST`
- 请求路径：`/api/developer-merchant/bind`
- 完整示例：`http://localhost:8877/api/developer-merchant/bind`
- `Content-Type`：`application/json`

### 业务说明

开发在前端选择商家后，点击绑定调用本接口。  
如果已绑定过，不重复插入，返回 `alreadyBound=true`。

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| developerId | number | 是 | 开发ID（`user_account.id`，且角色=4） |
| merchantId | number | 是 | 商家ID（`user_account.id`，且角色=3） |

请求体示例：

```json
{
  "developerId": 9,
  "merchantId": 3
}
```

### 成功响应示例

首次绑定：

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "developerId": 9,
    "merchantId": 3,
    "alreadyBound": false
  }
}
```

重复绑定：

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "developerId": 9,
    "merchantId": 3,
    "alreadyBound": true
  }
}
```

### 失败响应示例

开发不存在：

```json
{
  "code": "400",
  "msg": "开发不存在",
  "data": null
}
```

商家不存在：

```json
{
  "code": "400",
  "msg": "商家不存在",
  "data": null
}
```

---

## 2) 查询开发已绑定设备列表

### 接口信息

- 接口名称：开发已绑定设备列表
- 请求方式：`GET`
- 请求路径：`/api/developer-merchant/bound-list`
- 完整示例：`http://localhost:8877/api/developer-merchant/bound-list?developerId=9`

### 业务说明

查询某个开发账号已绑定商家下的设备列表。  
返回按“设备维度”展开（同一个商家有多台设备会返回多条）。

字段说明补充：

- `remainingUseCount`：商家当前剩余可用次数（来自 `user_account.remaining_use_count`）
- `merchantTotalDeviceUsageCount`：商家累计设备使用次数，统计来源 `usage_record`，计算方式为 `SUM(usage_count)`

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| developerId | number | 是 | 开发ID（`user_account.id`，且角色=4） |

### 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": [
    {
      "bindId": 12,
      "developerId": 9,
      "merchantId": 3,
      "merchantName": "王商家",
      "merchantPhone": "13900000001",
      "remainingUseCount": 88,
      "merchantTotalDeviceUsageCount": 356,
      "deviceId": 101,
      "machineNo": "0101",
      "deviceName": "王商家-1号设备",
      "deviceStatus": 1,
      "freeUseDeadline": "2026-12-31T23:59:59",
      "bindTime": "2026-04-26T17:52:00"
    },
    {
      "bindId": 10,
      "developerId": 9,
      "merchantId": 5,
      "merchantName": "星辰体态管理",
      "merchantPhone": "13900000002",
      "remainingUseCount": 120,
      "merchantTotalDeviceUsageCount": 212,
      "deviceId": 205,
      "machineNo": "0205",
      "deviceName": "星辰体态管理-5号设备",
      "deviceStatus": 1,
      "freeUseDeadline": null,
      "bindTime": "2026-04-26T17:30:00"
    }
  ]
}
```

### 失败响应示例

参数非法：

```json
{
  "code": "400",
  "msg": "developerId不能为空且必须大于0",
  "data": null
}
```

开发不存在：

```json
{
  "code": "400",
  "msg": "开发不存在",
  "data": null
}
```

---

## 前端调用示例（TypeScript）

```ts
const API_BASE = "http://localhost:8877";

export async function bindDeveloperMerchant(developerId: number, merchantId: number) {
  const res = await fetch(`${API_BASE}/api/developer-merchant/bind`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ developerId, merchantId })
  });
  const result = await res.json();
  if (result.code !== "0") throw new Error(result.msg || "绑定失败");
  return result.data as { developerId: number; merchantId: number; alreadyBound: boolean };
}

export async function fetchDeveloperBoundDevices(developerId: number) {
  const res = await fetch(`${API_BASE}/api/developer-merchant/bound-list?developerId=${developerId}`);
  const result = await res.json();
  if (result.code !== "0") throw new Error(result.msg || "查询失败");
  return result.data as Array<{
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
    deviceStatus: 0 | 1;
    freeUseDeadline: string | null;
    bindTime: string;
  }>;
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/developer-merchant/bind" \
  -H "Content-Type: application/json" \
  -d "{\"developerId\":9,\"merchantId\":3}"
```

```bash
curl "http://localhost:8877/api/developer-merchant/bound-list?developerId=9"
```
