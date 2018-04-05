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
    background: rgb(29, 35, 56);
    background: linear-gradient(
        45deg,
        rgba(77, 94, 144, 1) 0% rgba(29, 35, 56, 1) 100%
    );
    color: white;
}
</style>
