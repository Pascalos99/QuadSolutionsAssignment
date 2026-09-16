
<script setup>
  import { ref, onMounted, computed } from 'vue'
  import { decode}  from 'he'
  import JSConfetti  from 'js-confetti'

  const questions = ref(null)

  const numQuestions = ref(0)
  const numCorrect = ref(0)

  async function fetchData(amount) {
    questions.value = null;
    const res = await fetch(
      `/questions?count=${amount}`
    );
    questions.value = await res.json()

    questions.value = questions.value.map((question, i) => ({
      ...question,
      isEven: Boolean(i % 2),
      isDone: false,
      number: ++numQuestions.value,
      answers: question.answers.map((answer2, j) => ({
        text: answer2,
        state: 'active',
        position: j
      }))
    }))
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
    if (answer.state != 'active') return;
    let checked = await check(question, answer.text)
    for (let alt_answer of question.answers) {
      if (answer == alt_answer) {
        if (checked) {
          alt_answer.state = 'right';
          numCorrect.value++;
          showConfetti();
        }
        else {
          alt_answer.state = 'wrong'
        }
      }
      else {
        alt_answer.state = 'inactive'
      }
    }
    question.isDone = true;
  }

  async function loadMore() {
    fetchData(10)
  }

  const confetti = new JSConfetti()

  function showConfetti() {
    confetti.addConfetti()
  }

  const hideCompleted = ref(false)

  const filteredQuestions = computed(() => {
    return hideCompleted.value
      ? questions.value.filter((q) => !q.isDone)
      : questions.value
  })

  onMounted(loadMore)
</script>

<template>
  <div class="topbar">
    <button @click="loadMore">More Questions</button>
    <button @click="hideCompleted ^= true">{{ hideCompleted?"Show all":"Hide completed" }}</button>
    {{ numCorrect }}
  </div>
  <p v-if="!questions" class="content">Loading...</p>
  <div v-else class="content">
    <div class="questions">
      <div v-for="question in filteredQuestions" :key="question.uuid" class="question"
          :class="{ even: question.isEven, uneven: !question.isEven}">
        <div class="question-text">{{decode(question.question)}}</div>
        <div class="answers">
          <button v-for="answer in question.answers" class="answer" :class="answer.state, 'Q'+answer.position" @click="answerbutton(question, answer)">
            <b class="answer-text">{{ decode(answer.text) }}</b>
          </button>
        </div>
        <div>{{ question.number }}/{{ numQuestions }}</div>
      </div>
    </div>
  </div>
</template>

<style>
:root {
  --body-color: #3d7f47;
  --bg-color: #85a883;
  --bg-color2: #6c906b;
  --tx-color-a: #383838;
  --tx-color-q: #000000;
  --button-border: #62986a;
  --Q0: #63accb;
  --Q1: #F6987E;
  --Q2: #F6C241;
  --Q3: #EAF32F;
  --gray: #b0b0b0;
  --right: #72cb2a;
  --wrong: #696969;
}
body {
  background-color: var(--body-color);
}

button {
  border-color: var(--button-border);
  border-width: 1mm;
  background-color: var(--button-border);
}
</style>

<style scoped>
.topbar {
  display: flex;
  position: fixed;
  width: 100%;
  background-color: var(--body-color);
  left: 0%;
  top: 0%;
  border-color: var(--button-border);
  border-width: 1mm;
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
.content {
  display: flex;
  justify-content: center;
  column-gap: 5mm;
  margin-top: 15mm;
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
  font-size: 5.5mm;
  overflow-wrap: break-word;
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