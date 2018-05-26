<template>
  <div id="app">
    <Home />
    <particles />
  </div>
</template>

<script>
import Home from './components/Home';
import Particles from './components/Particles';

export default {
  name: 'App',
  components: {
    Home,
    Particles
  }
};
</script>

<style>
@import url('https://fonts.googleapis.com/css?family=Istok+Web');
@import url('https://fonts.googleapis.com/css?family=Lora');
* {
  font-family: 'Istok Web', sans-serif;
}
#app {
  width: 100%;
  height: 100%;
}
</style>
