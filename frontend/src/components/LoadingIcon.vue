<template>
  <div>
    <circle5 />
    <b-button>Cancel</b-button>
  </div>
</template>

<script>
import { Circle5 } from 'vue-loading-spinner'
import BButton from './includes/BButton'

export default {
  components: {
    Circle5,
    BButton
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
