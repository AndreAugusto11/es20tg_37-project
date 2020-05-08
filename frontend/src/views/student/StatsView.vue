<template>
  <div class="container">
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
