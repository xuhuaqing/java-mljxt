# 老师已绑定设备列表接口文档

## 基本信息

- 接口名称：老师已绑定设备列表
- 请求方式：`GET`
- 请求路径：`/api/teacher-device/bound-list`
- 完整示例：`http://localhost:8877/api/teacher-device/bound-list?teacherId=21`

## 业务说明

用于查询某个老师已绑定的设备列表，并展示设备对应的商家信息。  
支持按 `merchantId` 可选过滤。

## 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| teacherId | number | 是 | 老师ID（`user_account.id`，角色=2） |
| merchantId | number | 否 | 商家ID（`user_account.id`，角色=3），用于筛选该商家下绑定设备 |

请求示例：

- 查询老师全部已绑定设备  
  `GET /api/teacher-device/bound-list?teacherId=21`

- 查询老师在指定商家下已绑定设备  
  `GET /api/teacher-device/bound-list?teacherId=21&merchantId=3`

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": []
}
```

`data` 为数组，每项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| bindId | number | 绑定记录ID |
| teacherId | number | 老师ID |
| merchantId | number | 商家ID |
| merchantName | string | 商家名称 |
| deviceId | number | 设备ID |
| machineNo | string | 设备编号 |
| deviceName | string | 设备名称 |
| deviceStatus | number | 设备状态：`0` 停用，`1` 启用 |
| freeUseDeadline | string/null | 免费使用截至时间，格式 `yyyy-MM-ddTHH:mm:ss` |
| bindTime | string | 绑定时间，格式 `yyyy-MM-ddTHH:mm:ss` |

## 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": [
    {
      "bindId": 7,
      "teacherId": 21,
      "merchantId": 3,
      "merchantName": "王商家",
      "deviceId": 1,
      "machineNo": "0101",
      "deviceName": "王商家-1号设备",
      "deviceStatus": 1,
      "freeUseDeadline": "2026-12-31T23:59:59",
      "bindTime": "2026-04-26T14:10:00"
    },
    {
      "bindId": 8,
      "teacherId": 21,
      "merchantId": 3,
      "merchantName": "王商家",
      "deviceId": 2,
      "machineNo": "0102",
      "deviceName": "王商家-2号设备",
      "deviceStatus": 1,
      "freeUseDeadline": null,
      "bindTime": "2026-04-26T14:12:00"
    }
  ]
}
```

## 失败响应示例

### 1) 参数错误（teacherId为空或<=0）

```json
{
  "code": "400",
  "msg": "teacherId不能为空且必须大于0",
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

### 3) 商家不存在（传了merchantId但不存在）

```json
{
  "code": "400",
  "msg": "商家不存在",
  "data": null
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export async function fetchTeacherBoundDevices(teacherId: number, merchantId?: number) {
  const query = merchantId
    ? `?teacherId=${teacherId}&merchantId=${merchantId}`
    : `?teacherId=${teacherId}`;

  const res = await fetch(`${API_BASE}/api/teacher-device/bound-list${query}`);
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "获取老师已绑定设备失败");
  }
  return result.data as Array<{
    bindId: number;
    teacherId: number;
    merchantId: number;
    merchantName: string;
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
curl "http://localhost:8877/api/teacher-device/bound-list?teacherId=21"
```
