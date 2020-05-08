<template>
  <div class="container">
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalNumberSuggestions" data-cy="totalNumberSuggestions">
          <animated-number :number="stats.totalNumberSuggestions" />
        </div>
        <div class="project-name">
          <p>Number of suggestions made</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalNumberSuggestionsAvailable" data-cy="totalNumberSuggestionsAvailable">
          <animated-number :number="stats.totalNumberSuggestionsAvailable" />
        </div>
        <div class="project-name">
          <p>Number of suggestions accepted</p>
        </div>
      </div>
      <div class="items">
        <div class="items">
          <div class="icon-wrapper" ref="totalNumberCreatedTournaments" data-cy="totalNumberCreatedTournaments">
            <animated-number :number="stats.totalNumberCreatedTournaments" />
          </div>
          <div class="project-name">
            <p>Number of Tournaments created</p>
          </div>
        </div>
        <div class="items">
          <div class="icon-wrapper" ref="totalNumberEnrolledTournaments" data-cy="totalNumberEnrolledTournaments">
            <animated-number :number="stats.totalNumberEnrolledTournaments" />
          </div>
          <div class="project-name">
            <p>Number of Tournaments enrolled</p>
          </div>
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
      <div class="items">
        <div class="icon-wrapper" data-cy="totalClarificationRequests" ref="totalClarificationRequests">
          <animated-number :number="stats.totalClarificationRequests" />
        </div>
        <div class="project-name">
          <p>Total Clarification Requests</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" data-cy="totalPublicClarificationRequests" ref="totalPublicClarificationRequests">
          <animated-number :number="stats.totalPublicClarificationRequests" />
        </div>
        <div class="project-name">
          <p>Total Public Clarification Requests</p>
        </div>
      </div>
    </div>
    <v-card>
      <v-card dark color="primary">
        <v-card-title class="text-center justify-center py-6">
          <h1 class="font-weight-bold display-3 basil--text">Statistics</h1>
        </v-card-title>
        <v-tabs
                v-model="tabs"
                centered
                dark
                background-color="primary"
        >
          <v-tab>
            Quizzes
          </v-tab>
          <v-tab>
            Clarifications
          </v-tab>
          <v-tab>
            Suggestions
          </v-tab>
          <v-tab>
            Tournaments
          </v-tab>
        </v-tabs>
      </v-card>

      <v-tabs-items v-model="tabs">
        <v-tab-item>
          <v-card flat>
            <QuizStatsView :stats="stats"></QuizStatsView>
          </v-card>
        </v-tab-item>
        <v-tab-item>
          <v-card flat>
            <ClarificationStatsView :stats="stats"></ClarificationStatsView>
          </v-card>
        </v-tab-item>
        <v-tab-item>
          <v-card flat>
            <SuggestionStatsView :stats="stats"></SuggestionStatsView>
          </v-card>
        </v-tab-item>
        <v-tab-item>
          <v-card flat>
            <SuggestionStatsView :stats="stats"></SuggestionStatsView>
          </v-card>
        </v-tab-item>
      </v-tabs-items>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StudentStats from '@/models/statement/StudentStats';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import QuizStatsView from '@/views/QuizStatsView.vue';
import ClarificationStatsView from '@/views/ClarificationStatsView.vue';
import SuggestionStatsView from '@/views/SuggestionStatsView.vue';

@Component({
  components: { SuggestionStatsView, ClarificationStatsView, QuizStatsView, AnimatedNumber }
})
export default class StatsView extends Vue {
  stats: StudentStats | null = null;
  tabs = null;

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
}
</style>
