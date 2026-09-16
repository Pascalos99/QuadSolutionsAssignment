
<script setup>
  import { ref, watch } from 'vue'
  import { decode } from 'he'

  const qNumber = ref(0)
  const questions = ref([])

  function colorstyle(color) {
    return `background-color: var(--${color});`
  }

  const colors = ["red", "green", "blue", "yellow", "gray", "right", "wrong"]
  const colorstyles = []

  for (let color of colors) {
    colorstyles.push(colorstyle(color))
  }

  async function fetchData(amount) {
    questions.value = null;
    const res = await fetch(
      `/questions?count=${amount}`
    );
    questions.value = await res.json()

    for (let question of questions.value) {
      let res = []
      for (let i = 0; i < question.answers.length; i++) {
        res.push({
          answer: question.answers[i],
          styling: colorstyles[i],
          active: true
        })
      }
      question.answers = res;
    }
  }

  async function check(question, answer) {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        uuid: question.uuid,
        answer: answer
      })
    }
    const res = await fetch(`/checkanswers`, requestOptions);
    return await res.json();
  }

  async function answerbutton(question, answer) {
    if (!answer.active) return;
    let checked = await check(question, answer.answer)
    for (let alt_answer of question.answers) {
      alt_answer.active = false;
      if (answer == alt_answer) {
        if (checked) {
          alt_answer.styling = colorstyle("right");
        }
        else {
          alt_answer.styling = colorstyle("wrong");
        }
      }
      else {
        alt_answer.styling = colorstyle("gray");
      }
    }
  }

  // const requestOptions = {
  //   method: "POST",
  //   headers: { "Content-Type": "application/json" },
  //   body: JSON.stringify({ name: "Vue 3 POST Request Example" })
  // };

  watch(qNumber, ()=>fetchData(10))
  qNumber.value = 1
</script>

<template>
  <div class = "content"><button @click="qNumber++">More Questions</button></div>
  <p v-if="!questions" class="content">Loading...</p>
  <div v-else class="content">
    <div class="questions">
      <div v-for="question in questions" :key="question.uuid" class="question">
        <p>{{decode(question.question)}}</p>
        <div class="answers">
          <button v-for="answer in question.answers" class="answer" :style="answer.styling" @click="answerbutton(question, answer)">
            {{ decode(answer.answer) }}
          </button>
        </div>
        <br>
      </div>
    </div>
  </div>
</template>

<style>
:root {
  --bg-color: rgb(180, 203, 197);
  --tx-color-a: white;
  --tx-color-q: black;
  --red: rgb(163, 14, 14);
  --green: rgb(8, 191, 8);
  --blue: rgb(0, 140, 205);
  --yellow: rgb(255, 149, 0);
  --gray: gray;
  --right: green;
  --wrong: red;
}
</style>

<style scoped>
.content {
  display: flex;
  justify-content: center;
  padding-bottom: 2mm;
}
.questions {
  width: clamp(5cm, 95%, 50em);
  row-gap: 1cm;
}
.question {
  padding: 4mm;
  background-color: var(--bg-color);
  color: var(--tx-color-q);
  justify-content: center;
  text-align: center;
  width: clamp(1fr, 50%, 600px);
  font-size: 6mm;
}
.answers {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-auto-rows: max-content;
  gap: 1mm;
  justify-content: center;
}
.answer {
  text-align: center;
  align-content: center;
  color: var(--tx-color-a);
  background-color: var(--blue);
  border-radius: 6px;
  padding-bottom: 0.4em;
  padding-top: 0.4em;
}
</style>