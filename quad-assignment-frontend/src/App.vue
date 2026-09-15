
<script setup>
  import { ref, watch } from 'vue'

  const qNumber = ref(0)
  const questions = ref([])

  async function fetchData(amount) {
    questions.value = null;
    const res = await fetch(
      `/questions?count=3`
    );
    questions.value = await res.json()
  }

  // const requestOptions = {
  //   method: "POST",
  //   headers: { "Content-Type": "application/json" },
  //   body: JSON.stringify({ name: "Vue 3 POST Request Example" })
  // };

  watch(qNumber, fetchData)
  qNumber.value = 1
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
  <button @click="qNumber++">Next</button>
  <p v-if="!questions">Loading...</p>
  <ul v-else>
    <li v-for="question in questions" :key="question.uuid">{{question.question}}</li>
  </ul>
</template>

<style scoped>

</style>