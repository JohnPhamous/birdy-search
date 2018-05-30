<template>
  <div class="search-bar">
    <div class="card apply-opacity">
      <div class="card-content">
        <h1>Birdy Search</h1>

        <div v-if="!results.length">
          <form v-on:submit.prevent="submitQuery" v-if="!isLoading">
            <input type="text" v-model="query" placeholder="birds like cheese limit 25" />
            <b-button>Look Up</b-button>
            
            <div class="form-check">
              <input class="form-check-input" type="checkbox" @click="getLocation" v-model="searchByLocation" id="location">
              <label class="form-check-label" for="location">
                Search Near You
              </label>

            </div>
          </form>

          <div v-if="isLoading">
            Searching for: {{ query }}
            <loading-icon/>
          </div>
        </div>

        <div v-else>
          <button class="btn btn-sm back" @click="reset">Go Back</button>
          <result-list
            :results="results"
            :query="query"
            :latitude="latitude"
            :longitude="longitude"
            :totalMatched="totalMatched"
          >
          </result-list>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import ResultList from './ResultList';
import LoadingIcon from './LoadingIcon';
import BButton from './includes/BButton';

export default {
  data: function() {
    return {
      query: '',
      isLoading: false,
      apiBase: 'https://birdy.wls.ai/tweets?',
      searchByLocation: false,
      longitude: '',
      latitude: '',
      results: [],
      totalMatched: 0,
      numResults: 10
    };
  },
  methods: {
    getLocation: function() {
      if (!this.searchByLocation) {
        navigator.geolocation.getCurrentPosition(position => {
          this.latitude = position.coords.latitude;
          this.longitude = position.coords.longitude;
        });
      }
    },
    submitQuery: function() {
      this.isLoading = true;
      let query = this.apiBase;

      if (this.query.includes('limit')) {
        const delim = this.query.split('limit');
        this.numResults = delim[delim.length - 1];
      } else {
        this.numResults = 10;
      }

      query += `q=${this.query}&limit=${this.numResults}`;

      if (this.searchByLocation) {
        query += `&lat=${this.latitude}&lng=${this.longitude}`;
      }

      this.isLoading = false;

      axios
        .get(query)
        .then(response => {
          this.results = response.data.tweets;
          this.totalMatched = response.data.numResponses;
          this.isLoading = false;
        })
        .catch(error => {
          this.results = error;
        });
    },
    cancelSearch: function() {
      this.isLoading = false;
    },
    reset() {
      this.query = '';
      this.results = [];
      this.latitude = '';
      this.longitude = '';
      this.searchByLocation = false;
    }
  },
  components: {
    ResultList,
    LoadingIcon,
    BButton
  }
};
</script>

<style scoped>
.card {
  background: linear-gradient(45deg, #1e2339 0%, #4f6093 100%);
  margin-left: auto;
  margin-right: auto;
  border-radius: 20px;
  display: flex;
  justify-content: center;
  padding: 50px 100px;
  align-items: center;
}
.apply-opacity {
  opacity: 0.8;
}
.search-bar {
  text-align: center;
  display: flex;
  height: 100%;
  align-items: center;
  position: absolute;
  width: 100%;
}
.search-bar input[type='text'] {
  width: 30vw;
  background: white;
  border: none;
  border-radius: 10px;
  height: 30px;
  line-height: 20px;
  font-size: 20px;
  padding: 10px 20px;
  font-weight: 800;
  box-sizing: initial;
}
.back {
  position: absolute;
  top: 30px;
  left: 30px;
}
.form-check {
  text-align: right;
}
</style>
