<template>
  <div id="app">
    <Home />
  </div>
</template>

<script>
import Home from './components/Home'

export default {
  name: 'App',
  components: {
    Home
  }
}
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Istok+Web');
@import url('https://fonts.googleapis.com/css?family=Lora');
* {
  font-family: 'Istok Web', sans-serif;
}
p {
  font-family: 'Lora', serif;
}
#app {
  width: 100%;
  height: 100%;
}
</style>
