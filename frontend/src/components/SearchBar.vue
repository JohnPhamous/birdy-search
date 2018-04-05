<template>
  <div class="search-bar">
    <div class="card">
      <div class="card-content">
        <h1>Birdy Search</h1>
        <input type="text" placeholder="Twitter Search Query" />
        <button>Look Up</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style scoped>
.card {
    background: linear-gradient(45deg, #1e2339 0%, #4f6093 100%);
    height: 90vh;
    width: 95vw;
    display: block;
    margin-left: auto;
    margin-right: auto;
    border-radius: 20px;
}
.search-bar {
    text-align: center;
    display: flex;
    height: 100%;
    align-items: center;
}
.search-bar input[type='text'] {
    width: 50%;
    background: white;
    border: none;
    border-radius: 10px;
    height: 50px;
    line-height: 20px;
    font-size: 20px;
    padding: 10px 20px;
    font-weight: 800;
}
.search-bar button {
    height: 50px;
    font-size: 20px;
    font-weight: 800px;
    border-radius: 10px;
    padding: 10px 20px;
    border: none;
}
</style>
