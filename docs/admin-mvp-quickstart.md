# 后台管理系统MVP快速使用说明

## 1. 前端构建

在项目根目录执行：

```bash
cd admin-web
npm install
npm run build
```

构建产物会生成在 `admin-web/dist`。

## 2. 产物并入后端静态目录

将 `admin-web/dist` 内容复制到：

`src/main/resources/static/admin`

（当前工程已完成该目录结构）

## 3. 启动后端

```bash
mvnw.cmd spring-boot:run
```

服务默认端口：`8877`。

## 4. 访问后台

浏览器打开：

`http://127.0.0.1:8877/admin/`

## 5. 后台鉴权接口

- 登录：`POST /api/auth/login`
- 当前用户：`GET /api/admin/me`
- 菜单权限：`GET /api/admin/menus`

`/api/admin/**` 需要请求头：

`Authorization: Bearer <token>`

## 6. 说明

- 一期MVP为登录与角色权限，不含完整业务后台页面。
- 菜单根据角色静态返回，后续可升级为数据库配置。
