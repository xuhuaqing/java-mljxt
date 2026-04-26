# 商家下拉列表接口文档

## 基本信息

- 接口名称：商家下拉列表
- 请求方式：`GET`
- 请求路径：`/api/merchant/options`
- 完整示例：`http://localhost:8877/api/merchant/options`

## 业务说明

用于商家选择组件（下拉框）展示数据，返回商家 `id` 和 `name`。  
支持按商家名称模糊搜索。

## 请求参数

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| keyword | string | 否 | 商家名称关键字，模糊匹配 |

示例：

- 获取全部（最多100条）  
  `GET /api/merchant/options`
- 按关键字“美”搜索  
  `GET /api/merchant/options?keyword=美`

## 返回结构

统一返回体：

```json
{
  "code": "0",
  "msg": "success",
  "data": []
}
```

`data` 字段为数组，每项结构：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | number | 商家ID |
| name | string | 商家名称 |

## 成功响应示例

```json
{
  "code": "0",
  "msg": "success",
  "data": [
    {
      "id": 12,
      "name": "美丽健康中心"
    },
    {
      "id": 15,
      "name": "星辰体态管理"
    }
  ]
}
```

## 前端调用示例

```ts
const API_BASE = "http://localhost:8877";

export async function fetchMerchantOptions(keyword?: string) {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  const res = await fetch(`${API_BASE}/api/merchant/options${query}`);
  const result = await res.json();
  if (result.code !== "0") {
    throw new Error(result.msg || "获取商家下拉列表失败");
  }
  return result.data as Array<{ id: number; name: string }>;
}
```

## cURL 调试示例

```bash
curl "http://localhost:8877/api/merchant/options?keyword=美"
```
