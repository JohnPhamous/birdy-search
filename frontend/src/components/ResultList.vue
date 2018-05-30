<template>
  <div>
    <p><small>Showing {{ results.length }} out of {{ totalMatched }} matches for <strong>{{ query.split('limit')[0] }}</strong></small></p>
    <section class="results">
      <plots
        v-if="latitude && longitude"
        :data="results" 
        :centerLat="latitude"
        :centerLng="longitude"
        class="plots"
      />
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
import Plots from './Plots';

export default {
  props: ['results', 'query', 'latitude', 'longitude', 'totalMatched'],
  components: {
    Result,
    Plots
  }
};
</script>

<style scoped>
.results {
  overflow: scroll;
  height: 60vh;
}
.plots {
  margin-bottom: 30px;
}
</style>
