# 老师绑定设备接口文档

## 基本信息

- 接口名称：老师绑定设备
- 请求方式：`POST`
- 请求路径：`/api/teacher-device/bind`
- 完整示例：`http://localhost:8877/api/teacher-device/bind`

## 业务说明

老师在前端先选择商家，再选择该商家下的设备，点击绑定后调用本接口。  
接口会校验：

- 老师是否存在（且角色为老师）
- 商家是否存在（且角色为商家）
- 设备是否存在
- 设备是否属于当前选择商家

若同一个老师和同一台设备已经绑定过，不会重复插入，直接返回 `alreadyBound=true`。

## 请求参数

请求体 JSON：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| teacherId | number | 是 | 老师ID（`user_account.id`，角色=2） |
| merchantId | number | 是 | 商家ID（`user_account.id`，角色=3） |
| deviceId | number | 是 | 设备ID（`merchant_device.id`） |

请求示例：

```json
{
  "teacherId": 21,
  "merchantId": 3,
  "deviceId": 1
}
```

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": {}
}
```

`data` 字段结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| teacherId | number | 老师ID |
| merchantId | number | 商家ID |
| deviceId | number | 设备ID |
| alreadyBound | boolean | 是否已绑定过：`false`=本次新绑定，`true`=之前已绑定 |

## 成功响应示例

### 1) 首次绑定成功

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "teacherId": 21,
    "merchantId": 3,
    "deviceId": 1,
    "alreadyBound": false
  }
}
```

### 2) 重复绑定（幂等返回）

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "teacherId": 21,
    "merchantId": 3,
    "deviceId": 1,
    "alreadyBound": true
  }
}
```

## 失败响应示例

### 1) 参数校验失败

```json
{
  "code": "400",
  "msg": "teacherId必须大于0",
  "data": null
}
```

### 2) 老师不存在

```json
{
  "code": "400",
  "msg": "老师不存在",
  "data": null
}
```

### 3) 商家不存在

```json
{
  "code": "400",
  "msg": "商家不存在",
  "data": null
}
```

### 4) 设备不存在

```json
{
  "code": "400",
  "msg": "设备不存在",
  "data": null
}
```

### 5) 设备不属于该商家

```json
{
  "code": "400",
  "msg": "设备不属于该商家",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export type BindTeacherDevicePayload = {
  teacherId: number;
  merchantId: number;
  deviceId: number;
};

export async function bindTeacherDevice(payload: BindTeacherDevicePayload) {
  const res = await fetch(`${API_BASE}/api/teacher-device/bind`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  });
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "老师绑定设备失败");
  }
  return result.data as {
    teacherId: number;
    merchantId: number;
    deviceId: number;
    alreadyBound: boolean;
  };
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/teacher-device/bind" \
  -H "Content-Type: application/json" \
  -d "{\"teacherId\":21,\"merchantId\":3,\"deviceId\":1}"
```
