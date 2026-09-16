
<script setup>
  import { ref, onMounted } from 'vue'
  import { decode}  from 'he'
  import JSConfetti  from 'js-confetti'

  const questions = ref(null)

  const do_gaps = ref(true)

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

    // questions.value = questions.value.map((question, i) => 
    //   ({
    //     ...question,
    //     isEven: Boolean(i % 2),
    //     answers: question.answers.map((text, j) => ({
    //       ...text,
    //       styling: colorstyles[j],
    //       active: true
    //     }))
    //   })
    // );
    questions.value = questions.value.map((question, i) => ({
      ...question,
      isEven: Boolean(i % 2)
    }))
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
          showConfetti()
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

  async function loadMore() {
    fetchData(10)
  }

  const confetti = new JSConfetti()

  function showConfetti() {
    confetti.addConfetti()
  }

  onMounted(loadMore)
</script>

<template>
  <div class = "content">
    <button @click="loadMore">More Questions</button>
    <button @click="do_gaps ^= true">Switch Layout</button>
  </div>
  <p v-if="!questions" class="content">Loading...</p>
  <div v-else class="content">
    <div class="questions" :class="do_gaps? 'gaps' : 'nogaps'">
      <div v-for="question in questions" :key="question.uuid" class="question"
          :class="{ gaps: do_gaps, nogaps: !do_gaps, even: question.isEven, uneven: !question.isEven}">
        <div class="question-text">{{decode(question.question)}}</div>
        <div class="answers">
          <button v-for="answer in question.answers" class="answer" :style="answer.styling" @click="answerbutton(question, answer)">
            <b class="answer-text">{{ decode(answer.answer) }}</b>
          </button>
        </div>
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
  --button-border: #619268;
  --red: #63accb;
  --green: #F6987E;
  --blue: #F6C241;
  --yellow: #EAF32F;
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
.content {
  display: flex;
  justify-content: center;
  padding-bottom: 2mm;
  column-gap: 5mm;
}

.questions {
  width: clamp(5cm, 95%, 50em);
}
.questions.gaps {
  display: grid;
  row-gap: 5mm;
}

.question {
  background-color: var(--bg-color);
  color: var(--tx-color-q);
  text-align: center;
  padding-bottom: 5mm;
  padding-top: 2.5mm;
}
.question.nogaps {
  padding-bottom: 10mm;
}
.question.even {
  background-color: var(--bg-color2);
}

.question-text {
  padding-left: 5mm;
  padding-right: 5mm;
  padding-bottom: 2mm;
  font-size: 5.5mm;
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
.answer-text {

}
</style>