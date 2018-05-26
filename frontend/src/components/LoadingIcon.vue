<template>
  <div>
    <circle5 />
  </div>
</template>

<script>
import { Circle5 } from 'vue-loading-spinner'

export default {
  components: {
    Circle5,
  }
}
</script>

<style scoped>
.spinner {
  margin: 20px auto;
  overflow: inherit !important;
  display: block;
}
</style>
