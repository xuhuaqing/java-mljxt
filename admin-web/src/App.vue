<template>
  <router-view />
</template>

<style>
:root {
  --bg-page: #f2f5fb;
  --bg-card: #ffffff;
  --text-main: #1f2a37;
  --text-sub: #667085;
  --brand: #3b82f6;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", Arial, sans-serif;
  background: var(--bg-page);
  color: var(--text-main);
}
</style>
