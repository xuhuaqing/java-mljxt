<template>
  <div class="dashboard">
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-title">后台首页</div>
      <div class="welcome-sub">当前登录：{{ auth.me?.roleName }}（{{ auth.me?.phone }}）</div>
    </el-card>

    <el-card class="chart-card" shadow="never">
      <div class="chart-title">近7日访问趋势</div>
      <svg class="line-chart" viewBox="0 0 760 280" preserveAspectRatio="none">
        <polyline class="line" :points="linePoints" />
        <circle v-for="(p, i) in points" :key="i" class="dot" :cx="p.x" :cy="p.y" r="4" />
        <text v-for="(p, i) in points" :key="`t-${i}`" class="x-label" :x="p.x - 14" y="262">{{ p.label }}</text>
      </svg>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useAuthStore } from "../stores/auth";
const auth = useAuthStore();

const raw = [38, 52, 46, 68, 63, 74, 81];
const labels = ["周一", "周二", "周三", "周四", "周五", "周六", "周日"];
const max = Math.max(...raw) + 10;
const min = Math.min(...raw) - 8;

const points = computed(() =>
  raw.map((v, i) => {
    const x = 60 + i * 106;
    const y = 220 - ((v - min) / (max - min)) * 160;
    return { x, y, label: labels[i] };
  })
);

const linePoints = computed(() => points.value.map((p) => `${p.x},${p.y}`).join(" "));
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.welcome-card {
  border-radius: 14px;
  border: 1px solid #e8eef8;
}

.welcome-title {
  font-size: 18px;
  font-weight: 600;
}

.welcome-sub {
  margin-top: 8px;
  color: #667085;
}

.chart-card {
  border-radius: 14px;
  border: 1px solid #e8eef8;
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 8px;
}

.line-chart {
  width: 100%;
  height: 300px;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 65%);
  border-radius: 10px;
}

.line {
  fill: none;
  stroke: #3b82f6;
  stroke-width: 3;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.dot {
  fill: #3b82f6;
  stroke: #ffffff;
  stroke-width: 2;
}

.x-label {
  font-size: 12px;
  fill: #64748b;
}
</style>
