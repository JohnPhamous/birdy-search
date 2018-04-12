<template>
  <div class="search-bar">
    <div class="card">
      <div class="card-content">
        <h1>Birdy Search</h1>
        <form v-on:submit.prevent="submitQuery">
          <input type="text" v-model="query" placeholder="Twitter Search Query" />
          <button class="btn">Look Up</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import ResultList from './ResultList'

export default {
  data: function() {
    return {
      query: ''
    }
  },
  methods: {
    submitQuery: function() {
      console.log('Submitting Query', this.query)
    }
  },
  components: {
    ResultList
  }
}
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
.search-bar {
  text-align: center;
  display: flex;
  height: 100%;
  align-items: center;
}
.search-bar input[type='text'] {
  width: 50vw;
  background: white;
  border: none;
  border-radius: 10px;
  height: 30px;
  line-height: 20px;
  font-size: 20px;
  padding: 10px 20px;
  font-weight: 800;
}
.search-bar button {
  height: 50px;
  font-size: 20px;
  font-weight: 800;
  border-radius: 10px;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
}
.btn {
  flex: 1 1 auto;
  margin: 10px;
  padding: 30px;
  text-align: center;
  text-transform: uppercase;
  transition: 0.5s;
  background-size: 200% auto;
  color: white;
  background-image: linear-gradient(
    to right,
    rgb(133, 138, 243) 0%,
    rgb(193, 73, 236) 51%,
    rgb(238, 96, 190) 100%
  );
}
.btn:hover {
  background-position: right center; /* change the direction of the change here */
}
</style>
