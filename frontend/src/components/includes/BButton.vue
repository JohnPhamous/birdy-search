<template>
  <button class="btn">
    <slot></slot>
  </button>
</template>

<script>
export default {};
</script>

<style scoped>
.btn {
  border: none;
  flex: 1 1 auto;
  margin: 10px;
  height: 50px;
  font-size: 20px;
  font-weight: 800;
  border-radius: 10px;
  padding: 10px 20px;
  border: none;
  cursor: pointer;
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
