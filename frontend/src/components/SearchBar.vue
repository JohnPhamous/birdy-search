<template>
  <div class="search-bar">
    <div class="card apply-opacity">
      <div class="card-content">
        <h1>Birdy Search</h1>

        <div v-if="!results.length">
          <form v-on:submit.prevent="submitQuery" v-if="!isLoading">
            <input type="text" v-model="query" placeholder="Twitter Search Query" />
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
          <result-list :results="results" :query="query">
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
      results: []
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
      query += `q=${this.query}`;

      console.log(this.latitude);
      if (this.searchByLocation) {
        query += `&lat=${this.latitude}&lng=${this.longitude}`;
      }

      console.log(query);

      setTimeout(() => {
        this.isLoading = false;

        this.results = [
          {
            id_str: '984178502386712576',
            text: 'bacon pancakes, makin bacon pancakes',
            location: { lat: -14.239565499999998, lng: -53.184834 },
            truncated: false,
            timestamp: 1523481414778,
            user: { screen_name: 'prs1_' }
          },
          {
            id_str: '984853738958610432',
            text: 'Bacon 🤤',
            location: { lat: -27.487032999999997, lng: -58.796132 },
            truncated: false,
            timestamp: 1523642403722,
            user: { screen_name: '_Yaquichan' }
          },
          {
            id_str: '984200641538088962',
            text:
              'Dat bacon, bacon, bacon burger tho 🍔🥓 @ Clinton Hall 51 https://t.co/CK5BwH20BS',
            title:
              '\nManhattan with a Twist on Instagram: “Dat bacon, bacon, bacon burger tho 🍔🥓”\n',
            location: { lat: 40.75590964, lng: -73.96902335 },
            truncated: false,
            timestamp: 1523486693163,
            user: { screen_name: 'ManhattanTwist' }
          },
          {
            id_str: '984252963479801856',
            text: 'Bacon cheddar',
            location: { lat: 36.61234949999999, lng: -88.32464949999999 },
            truncated: false,
            timestamp: 1523499167686,
            user: { screen_name: 'misanthropester' }
          },
          {
            id_str: '984492797901320192',
            text: 'BACON SUPREME',
            location: { lat: -20.8753275, lng: -54.261875 },
            truncated: false,
            timestamp: 1523556348668,
            user: { screen_name: 'luaxj_' }
          },
          {
            id_str: '984183254919004160',
            text: '@Donna_McCoy Bacon?',
            location: { lat: 45.530778999999995, lng: -122.93553 },
            truncated: false,
            timestamp: 1523482547870,
            user: { screen_name: 'mane_mbsmith' }
          },
          {
            id_str: '984875364697755648',
            text: 'Bacon is addictive',
            location: { lat: 38.7595095, lng: -90.4276455 },
            truncated: false,
            timestamp: 1523647559700,
            user: { screen_name: 'CasinoSSB' }
          },
          {
            id_str: '986035752785018881',
            text: '@themeatly BACON.',
            location: { lat: -33.531122499999995, lng: -70.527119 },
            truncated: false,
            timestamp: 1523924217779,
            user: { screen_name: 'Ghosty_Pau' }
          },
          {
            id_str: '984853412759113728',
            text: 'bacon da morte',
            location: { lat: -20.280410500000002, lng: -40.292823 },
            truncated: false,
            timestamp: 1523642325950,
            user: { screen_name: 'wewont_' }
          },
          {
            id_str: '986035026021289984',
            text: 'Macarrão com bacon 😋😋😋😋😋😋😋😋😋😋😋😋😋😋😋😋😋😋',
            location: { lat: -22.911421500000003, lng: -43.441578 },
            truncated: false,
            timestamp: 1523924044505,
            user: { screen_name: 'Dryeduarda15' }
          }
        ];
      }, 1000);
      // axios
      //   .get(query)
      //   .then(response => {
      //     this.results = response.data;
      //     this.isLoading = false;
      //   })
      //   .catch(error => {
      //     this.results = error;
      //   });
    },
    cancelSearch: function() {
      this.isLoading = false;
    },
    reset() {
      this.query = '';
      this.results = [];
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
