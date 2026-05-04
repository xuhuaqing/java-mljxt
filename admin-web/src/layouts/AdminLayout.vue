<template>
  <el-container class="layout-root">
    <el-aside width="236px" class="layout-aside">
      <div class="brand-wrap">
        <div class="brand-logo">M</div>
        <div>
          <div class="brand-title">美丽肌线条 后台管理系统</div>
        </div>
      </div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item v-for="m in auth.menus" :key="m.path" :index="m.path">{{ m.name }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="layout-header">
        <div>
          <div class="header-title">欢迎回来</div>
          <div class="header-sub">当前身份：{{ auth.me?.roleName }}</div>
        </div>
        <el-button class="logout-btn" @click="logout">退出登录</el-button>
      </el-header>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();
function logout() {
  auth.logout();
  router.replace("/login");
}
</script>

<style scoped>
.layout-root {
  min-height: 100vh;
}

.layout-aside {
  border-right: 1px solid #eef2f7;
  background: #ffffff;
  box-shadow: 2px 0 18px rgba(31, 41, 55, 0.03);
}

.brand-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 16px 14px;
  border-bottom: 1px solid #f0f3f8;
}

.brand-logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
}

.brand-title {
  font-size: 15px;
  font-weight: 600;
}

.menu {
  border-right: none;
  padding: 10px 8px;
}

.layout-header {
  height: 68px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 22px;
  background: #fff;
  border-bottom: 1px solid #eef2f7;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
}

.header-sub {
  margin-top: 2px;
  font-size: 12px;
  color: #667085;
}

.logout-btn {
  border-radius: 10px;
}

.layout-main {
  padding: 18px;
  background: #f2f5fb;
}
</style>
