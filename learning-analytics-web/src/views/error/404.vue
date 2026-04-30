<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.error-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-page);

  .error-content {
    text-align: center;

    .error-code {
      font-size: 120px;
      font-weight: 700;
      color: var(--color-primary);
      line-height: 1;
      margin-bottom: 16px;
    }

    .error-message {
      font-size: 18px;
      color: var(--color-text-secondary);
      margin-bottom: 32px;
    }
  }
}
</style>
