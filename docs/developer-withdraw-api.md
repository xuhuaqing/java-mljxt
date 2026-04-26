# 开发提现接口文档

## 基本信息

- 模块：开发提现
- 统一返回：`{"code":"","msg":"","data":{}}`
- 角色约束：开发角色 `role=4`

---

## 1) 点击提现（生成记录）

### 接口信息

- 接口名称：开发提现
- 请求方式：`POST`
- 请求路径：`/api/developer-merchant/withdraw`
- 完整示例：`http://localhost:8877/api/developer-merchant/withdraw`
- `Content-Type`：`application/json`

### 业务说明

开发点击提现后，系统会生成一条提现记录。  
记录中会写入当前时刻“该开发名下所有商家的使用次数之和”（快照值）。

幂等规则：同一个开发账号一天仅允许提现一次（自然日）。

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| developerId | number | 是 | 开发ID（`user_account.id`，角色=4） |

请求体示例：

```json
{
  "developerId": 4
}
```

### 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "withdrawRecordId": 15,
    "developerId": 4,
    "usageCountSnapshot": 568,
    "createdAt": "2026-04-26T18:25:00"
  }
}
```

### 失败响应示例

```json
{
  "code": "400",
  "msg": "开发不存在",
  "data": null
}
```

```json
{
  "code": "400",
  "msg": "今日已提现，请明天再试",
  "data": null
}
```

---

## 2) 提现记录列表

### 接口信息

- 接口名称：开发提现记录列表
- 请求方式：`GET`
- 请求路径：`/api/developer-merchant/withdraw-records`
- 完整示例：`http://localhost:8877/api/developer-merchant/withdraw-records?developerId=4&pageNo=1&pageSize=10`

### 业务说明

按开发ID查询提现记录，支持分页。  
排序规则：按 `createdAt` 倒序（最新记录优先）。

### 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| developerId | number | 是 | 开发ID（`user_account.id`，角色=4） |
| pageNo | number | 否 | 页码，默认 `1` |
| pageSize | number | 否 | 每页条数，默认 `10`，最大 `100` |

### 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": {
    "total": 3,
    "pageNo": 1,
    "pageSize": 10,
    "records": [
      {
        "withdrawRecordId": 15,
        "developerId": 4,
        "usageCountSnapshot": 568,
        "createdAt": "2026-04-26T18:25:00"
      },
      {
        "withdrawRecordId": 12,
        "developerId": 4,
        "usageCountSnapshot": 521,
        "createdAt": "2026-04-26T17:30:00"
      }
    ]
  }
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

export async function developerWithdraw(developerId: number) {
  const res = await fetch(`${API_BASE}/api/developer-merchant/withdraw`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ developerId })
  });
  const result = await res.json();
  if (result.code !== "0") throw new Error(result.msg || "提现失败");
  return result.data;
}

export async function fetchDeveloperWithdrawRecords(
  developerId: number,
  pageNo = 1,
  pageSize = 10
) {
  const res = await fetch(
    `${API_BASE}/api/developer-merchant/withdraw-records?developerId=${developerId}&pageNo=${pageNo}&pageSize=${pageSize}`
  );
  const result = await res.json();
  if (result.code !== "0") throw new Error(result.msg || "查询提现记录失败");
  return result.data;
}
```

## cURL 调试示例

```bash
curl -X POST "http://localhost:8877/api/developer-merchant/withdraw" \
  -H "Content-Type: application/json" \
  -d "{\"developerId\":4}"
```

```bash
curl "http://localhost:8877/api/developer-merchant/withdraw-records?developerId=4&pageNo=1&pageSize=10"
```
