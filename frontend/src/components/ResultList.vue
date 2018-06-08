<template>
  <div>
    <p><small>Showing {{ results.length }} out of {{ totalMatched }} matches for <strong>{{ query.split('limit')[0] }}</strong></small></p>
    <section class="results">
      <div v-if="isLoading">
        <loading-icon/>
      </div>

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
    <section id="pagination">
      <span
        class="previous text-btn"
        @click="changePageByOne(-1)"
      >
        Previous
      </span>
      <button
        v-for="i in getNumPages"
        :key="i"
        @click="getPage(i)"
        class="pagination-btn"
        :class="{ active : i === currentPage}"
        
      >
        {{ i }}
      </button>
      <span
        class="text-btn"
        @click="changePageByOne(1)"
      >
        Next
      </span>
    </section>
  </div>
</template>

<script>
import Result from './Result';
import Plots from './Plots';
import axios from 'axios';
import LoadingIcon from './LoadingIcon';

export default {
  props: ['results', 'query', 'latitude', 'longitude', 'totalMatched'],
  components: {
    Result,
    Plots,
    LoadingIcon
  },
  methods: {
    getPage(pageNum) {
      this.isLoading = true;
      this.currentPage = pageNum;
      const offset = (pageNum - 1) * 10;

      let newQuery = `https://birdy.wls.ai/tweets?`;
      newQuery += `q=${this.query}`;

      if (this.latitude && this.longitude) {
        newQuery += `&lat=${this.latitude}&lng=${this.longitude}`;
      }

      newQuery += `&offset=${offset}`;

      axios
        .get(newQuery)
        .then(res => {
          this.results = res.data.tweets;
          this.isLoading = false;
        })
        .catch(error => {
          console.log(error);
        });
    },
    changePageByOne(offset) {
      if (
        this.currentPage + offset == 0 ||
        this.currentPage + offset > this.getNumPages
      ) {
        return;
      } else {
        this.getPage(this.currentPage + offset);
      }
    }
  },
  computed: {
    getNumPages() {
      const numPages = Math.floor(this.totalMatched / 10);

      if (numPages > 22) {
        return 22;
      } else {
        return numPages;
      }
    }
  },
  data: function() {
    return {
      currentPage: 1,
      isLoading: false
    };
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
#pagination {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.pagination-btn {
  border: none;
  cursor: pointer;
  border-radius: 5px;
  margin-right: 10px;
}
.active {
  background: #afd1f6;
  border: 3px white solid;
  border-radius: 25px;
  font-weight: 800;
}
.previous {
  margin-right: 10px;
}
.text-btn {
  cursor: pointer;
}
</style>
