<template>
  <div class="login-page">
    <div class="login-left">
      <div class="hello-title">MLJXT 后台管理系统</div>
      <div class="hello-sub">统一后台账号登录，集中查看用户、老师、商家、开发四类账号数据。</div>
    </div>
    <el-card class="login-card" shadow="never">
      <template #header>
        <div class="card-header">后台登录</div>
      </template>
      <el-form :model="form" label-position="top">
        <el-form-item label="账号">
          <el-input v-model="form.account" size="large" placeholder="请输入后台账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" size="large" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="submit-btn" @click="submit">登录系统</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { useAuthStore } from "../stores/auth";
import { useRouter } from "vue-router";

const loading = ref(false);
const router = useRouter();
const auth = useAuthStore();
const form = reactive({
  account: "",
  password: ""
});

async function submit() {
  loading.value = true;
  try {
    await auth.doLogin(form);
    await auth.loadProfile();
    await router.replace("/dashboard");
    ElMessage.success("登录成功");
  } catch (e: any) {
    ElMessage.error(e?.message || "登录失败");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 1fr 420px;
  align-items: center;
  gap: 60px;
  padding: 0 10%;
  background: radial-gradient(circle at 20% 20%, #dbeafe 0%, #f8fafc 45%, #f3f6fb 100%);
}

.login-left {
  max-width: 560px;
}

.hello-title {
  font-size: 38px;
  line-height: 1.3;
  font-weight: 700;
  color: #111827;
}

.hello-sub {
  margin-top: 14px;
  font-size: 15px;
  line-height: 1.8;
  color: #64748b;
}

.login-card {
  border-radius: 16px;
  border: 1px solid #e9eef7;
  box-shadow: 0 16px 40px rgba(15, 23, 42, 0.08);
}

.card-header {
  font-size: 18px;
  font-weight: 600;
}

.submit-btn {
  width: 100%;
  border-radius: 10px;
}
</style>
