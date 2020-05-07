<template>
  <div class="container">
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalNumberSuggestions">
          <animated-number :number="stats.totalNumberSuggestions" />
        </div>
        <div class="project-name">
          <p>Number of suggestions made</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalNumberSuggestionsAvailable">
          <animated-number :number="stats.totalNumberSuggestionsAvailable" />
        </div>
        <div class="project-name">
          <p>Number of suggestions accepted</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.totalQuizzes" />
        </div>
        <div class="project-name">
          <p>Total Quizzes Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.totalAnswers" />
        </div>
        <div class="project-name">
          <p>Total Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalUniqueQuestions">
          <animated-number :number="stats.totalUniqueQuestions" />
        </div>
        <div class="project-name">
          <p>Unique Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number :number="stats.correctAnswers">%</animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="improvedCorrectAnswers">
          <animated-number :number="stats.improvedCorrectAnswers"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Improved Correct Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfSeenQuestions">
          <animated-number
            :number="
              (stats.totalUniqueQuestions * 100) / stats.totalAvailableQuestions
            "
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of questions seen</p>
        </div>
      </div>
    </div>
    <div class="toggle-btn">
      <div class="inner-circle">
        onclick= "this.classList.toggle('active')">
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StudentStats from '@/models/statement/StudentStats';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

@Component({
  components: { AnimatedNumber }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getUserStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }

  .toggle-btn {
    width: 80px;
    height: 40px;
    background: gray;
    border-radius: 30px;
    padding: 5px;
    transition: all 300ms ease-in-out;
  }

  .toggle-btn > .inner-circle {
    width: 40px;
    height: 40px;
    background: #fff;
    border-radius: 50%;
    padding: 5px;
    transition: all 300ms ease-in-out;
  }

  .toggle-btn.active {
    background: #00acee;
  }

  .toggle-btn.active > .inner-circle {
    margin-left: 40px;
  }
}
</style>
