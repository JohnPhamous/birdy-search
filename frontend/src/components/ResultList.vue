<template>
  <div>
    <p><small>There are {{ results.length }} matches for <strong>{{ query }}</strong>.</small></p>
    <section class="results">
      <result
        v-for="r in results"
        :tweet="r"
        :key="r.id_str"
        :query="query"
      />
    </section>
  </div>
</template>

<script>
import Result from './Result';

export default {
  props: ['results', 'query'],
  components: {
    Result
  }
};
</script>

<style scoped>
.results {
  overflow: scroll;
  height: 60vh;
}
</style>
