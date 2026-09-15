<script setup>
  import { ref, watch } from 'vue'

  const todoId = ref(1);
  const todoData = ref(null);

  async function fetchData() {
    todoData.value = null;
    const res = await fetch(
      `/questions`
    );
    todoData.value = await res.json();
  }

  fetchData();
  watch(todoId, fetchData)
</script>

<template>
  <h1>You did it!</h1>
  <p>
    Visit <a href="https://vuejs.org/" target="_blank" rel="noopener">vuejs.org</a> to read the
    documentation
  </p>
  <p>
    There is a test page at <a href="/biba/index.html" target="_blank">biba</a>
  </p>
  <button @click="todoId++">Next</button>
  <p v-if="!todoData">Loading...</p>
  <p v-else>{{ todoData }}</p>
</template>

<style scoped>

</style>