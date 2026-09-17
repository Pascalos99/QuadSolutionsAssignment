
<script setup>
  import { ref, onMounted, computed } from 'vue'
  import { decode}  from 'he'
  import JSConfetti  from 'js-confetti'

  const questions = ref(null)
  const loading = ref(false)
  const token = ref(null)

  const numQuestions = ref(0)
  const numCorrect = ref(0)
  const numWrong = ref(0)

  const hideCompleted = ref(false)
  const scoreShowNumCorrect = ref(true)

  const delay = ms => new Promise(res => setTimeout(res, ms));
  const confetti = new JSConfetti()
  const showConfetti = () => confetti.addConfetti()

  function processQuestion(question) {
    return {
      ...question,
      isDone: false,
      number: ++numQuestions.value,
      answers: question.answers.map((answer, i) => ({
        text: answer,
        state: 'active',
        position: i
      }))
    }
  }

  async function fetchData(amount) {
    let request = `/questions?count=${amount}`
    if (token.value) {
      request = `/questions?count=${amount}&token=${token.value}`
    }
    const res = await fetch(request);
    if (res.ok) {
      let result = await res.json()
      if (!questions.value) {
        questions.value = [];
      }
      questions.value = questions.value.map((question) => ({
        ...question
      }))
      questions.value = questions.value.concat(result.questions.map(processQuestion));
      token.value = result.token;
      return true;
    } else {
      return false;
    }
  }

  const answerCorrect = 1, answerIncorrect = 2, answerExpired = 3;

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
    if (!res.ok) return answerExpired;
    return (await res.json()) ? answerCorrect : answerIncorrect;
  }

  async function sendAnswer(question, answer) {
    if (answer.state !== 'active') return;
    let checked = await check(question, answer.text)
    for (let alt_answer of question.answers) {
      if (answer === alt_answer) {
        if (checked === answerCorrect) {
          alt_answer.state = 'right';
          numCorrect.value++;
          showConfetti();
        }
        else if (checked === answerIncorrect) {
          alt_answer.state = 'wrong'
          numWrong.value++;
        } else {
          // question has expired, don't count it
          alt_answer.state = 'inactive'
        }
      }
      else {
        alt_answer.state = 'inactive'
      }
    }
    question.isDone = true;
  }

  async function loadMore() {
    if (loading.value) return;
    loading.value = true;
    while (!(await fetchData(10))) {
      // This condition is mainly met whenever the server is down
      // Regular requests consistently return a valid response
      await delay(1000)
    }
    loading.value = false;
  }

  const filteredQuestions = computed(() => {
    return hideCompleted.value
      ? questions.value.filter((q) => !q.isDone)
      : questions.value
  })

  onMounted(loadMore)
</script>

<template>
  <div class="topbar">
    <button @click="loadMore" :disabled="loading">More Questions</button>
    <button @click="hideCompleted ^= true">{{ hideCompleted?"Show all":"Hide completed" }}</button>
    <button @click="scoreShowNumCorrect ^= true" class="counter">
      <div v-if="scoreShowNumCorrect">Score: {{ numCorrect }}</div>
      <div v-else>Wrong: {{ numWrong }}</div>
    </button>
  </div>
  <div class="main">
    <p v-if="loading && !questions" class="content loading">Loading...</p>
    <div v-else class="content">
      <div class="questions">
        <div v-for="(question, i) in filteredQuestions" :key="question.uuid" class="question"
            :class="{ even: i % 2 === 1 }">
          <div class="question-text">{{decode(question.question)}}</div>
          <div class="answers">
            <button v-for="answer in question.answers" class="answer" :class="answer.state, 'Q'+answer.position" @click="sendAnswer(question, answer)">
              <b class="answer-text">{{ decode(answer.text) }}</b>
            </button>
          </div>
          <div>{{ question.number }}/{{ numQuestions }}</div>
        </div>
      </div>
    </div>
    <div class="tail"></div>
  </div>
</template>

<style>
:root {
  --body1-color: #3d7f47;
  --body2-color: #f9c638;
  --bg-color: #85a883;
  --bg-color2: #6c906b;
  --tx-color-a: #383838;
  --tx-color-q: #000000;
  --button-border: #62986a;
  --counter-bg: #ffd270;
  --counter-border: #d1aa57;
  --Q0: #63accb;
  --Q1: #F6987E;
  --Q2: #F6C241;
  --Q3: #EAF32F;
  --gray: #b0b0b0;
  --right: #72cb2a;
  --wrong: #696969;
  --font: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  --fsize-small: 1em;
  --fsize-large: 1.4em;
}
body {
  background-color: var(--body1-color);
  background: linear-gradient(0deg,var(--body2-color) 0%,var(--body1-color) 100%);
  font-family: var(--font);
  font-size: var(--fsize-small);
}

button {
  border-color: var(--button-border);
  border-width: 1mm;
  background-color: var(--button-border);
  font-family: var(--font);
  font-size: var(--fsize-small);
}
</style>

<style scoped>
.topbar {
  display: flex;
  position: fixed;
  width: 100%;
  background-color: var(--body1-color);
  left: 0%;
  top: 0%;
  border-color: var(--button-border);
  border-width: 1.5mm;
  border-left: 0;
  border-right: 0;
  border-top: 0;
  border-style: solid;
  justify-content: center;
  column-gap: 2.5%;
  padding: 2mm;
  padding-right: 0;
  padding-left: 0;
  margin-left: 0;
  margin-right: 0;
}
.counter {
	background-color: var(--counter-bg);
  border-color: var(--counter-border);
}
.main {
  min-height: calc(100vh - 3.5em);
  display: flex;
  flex-direction: column;
  padding-top: 3.5em;
}
.content {
  display: flex;
  justify-content: center;
  column-gap: 5mm;
}
.tail {
  flex: 1;
}

.loading {
  background-color: var(--bg-color);
  color: var(--tx-color-q);
  font-size: var(--fsize-large);
  padding: 5mm;
  width: fit-content;
  align-self: center;
  border-radius: 2mm;
  border-style: solid;
  border-color: var(--bg-color2);
  border-width: 1mm;
}

.questions {
  width: clamp(5cm, 95%, 50em);
  display: grid;
  row-gap: 5mm;
  justify-content: center;
  overflow-wrap: break-word;
}
.question {
  display: grid;
  row-gap: 2mm;
  background-color: var(--bg-color);
  border-radius: 2mm;
  color: var(--tx-color-q);
  text-align: center;
  padding-bottom: 3mm;
  padding-top: 2.5mm;
  overflow-wrap: break-word;
}
.question.even {
  background-color: var(--bg-color2);
}
.question-text {
  padding-left: 5mm;
  padding-right: 5mm;
  padding-bottom: 1mm;
  overflow-wrap: break-word;
  font-size: var(--fsize-large);
}

.answers {
  display: grid;
  width: 75%;
  justify-self: center;
  grid-template-columns: 1fr 1fr;
  gap: 2mm;
  justify-content: center;
}
.answer {
  text-align: center;
  align-content: center;
  color: var(--tx-color-a);
  border-radius: 3mm;
  padding-bottom: 0.5em;
  padding-top: 0.5em;
}
/* .answer-text {} */
.answer.Q0 {
  background-color: var(--Q0);
}
.answer.Q1 {
  background-color: var(--Q1);
}
.answer.Q2 {
  background-color: var(--Q2);
}
.answer.Q3 {
  background-color: var(--Q3);
}
.answer.inactive {
  background-color: var(--gray);
}
.answer.right {
  background-color: var(--right);
}
.answer.wrong {
  background-color: var(--wrong);
}
</style>