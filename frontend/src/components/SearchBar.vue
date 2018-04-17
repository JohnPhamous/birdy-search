<template>
  <div class="search-bar">
    <div class="card">
      <div class="card-content">
        <h1>Birdy Search</h1>

        <form v-on:submit.prevent="submitQuery" v-if="!isLoading">
          <input type="text" v-model="query" placeholder="Twitter Search Query" />
          <b-button>Look Up</b-button>
        </form>

        <loading-icon v-if="isLoading" />
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import ResultList from './ResultList'
import LoadingIcon from './LoadingIcon'
import BButton from './includes/BButton'

export default {
  data: function() {
    return {
      query: '',
      isLoading: false
    }
  },
  methods: {
    submitQuery: function() {
      this.isLoading = true
    }
  },
  components: {
    ResultList,
    LoadingIcon,
    BButton
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
}
</style>
