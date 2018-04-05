<template>
  <search-bar />
</template>

<script>
import SearchBar from './SearchBar'
export default {
    components: {
        SearchBar
    }
}
</script>

<style>
html,
body {
    background: rgb(253, 103, 169);
    background: linear-gradient(
        45deg,
        rgba(253, 103, 169, 1) 0%,
        rgba(147, 186, 254, 1) 100%
    );
    color: white;
    font-family: 'Gill Sans', 'Gill Sans MT', Calibri, 'Trebuchet MS',
        sans-serif;
    height: 100%;
    width: 100%;
    margin: 0;
}
</style>
