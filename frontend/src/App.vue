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
#app {
    width: 100%;
    height: 100%;
}
</style>
