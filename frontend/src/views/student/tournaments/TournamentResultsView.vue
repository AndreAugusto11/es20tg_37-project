<template>
  <v-card
    class="table"
    max-width="80%"
    style="margin-right: auto; margin-left: auto;"
  >
    <v-card v-if="tournament" dark color="primary">
      <v-card-title class="text-left text-capitalize justify-left py-6">
        <h1 class="font-weight-bold display-2">🏆 {{ tournament.title }}</h1>
      </v-card-title>
      <v-card-title class="text-left text-capitalize justify-left py-6">
        <span>Created by {{ tournament.creatorName }}</span>
      </v-card-title>
    </v-card>
    <v-data-table
      :headers="headers"
      :items="tournamentResults"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      max-width="500px"
      class="large-table"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn
            color="primary"
            dark
            @click="showResults"
            data-cy="showResultsButton"
          >
            See my Quiz
          </v-btn>
        </v-card-title>
      </template>

      <template v-slot:item="{ item }">
        <tr>
          <td>{{ tournamentResults.indexOf(item) + 1 }}</td>
          <td style="text-align: left">
            {{ item.enrolledStudentUsername }}
          </td>
          <td>{{ item.numberOfCorrectAnswers }}</td>
          <td>{{ item.timeTaken }}</td>
        </tr>
      </template>

      <template v-slot:item.student="{ item }">
        <v-chip :color="getNumberColor(item)" small>
          <span>{{ tournamentResults.indexOf(item) + 1 }}.</span>
        </v-chip>
        <span>
          {{ item.enrolledStudentUsername }}
        </span>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementManager from '@/models/statement/StatementManager';
import { TournamentResult } from '@/models/tournaments/TournamentResult';

@Component
export default class TournamentResultsView extends Vue {
  tournament: Tournament | null = null;
  tournamentResults: TournamentResult[] = [];
  search: string = '';
  headers: object = [
    {
      text: '',
      value: 'place',
      align: 'center',
      width: '1%'
    },
    {
      text: 'Student',
      value: 'student',
      align: 'left',
      width: '60%'
    },
    {
      text: 'Number Of Correct Answers',
      value: 'numberOfCorrectAnswers',
      align: 'center',
      width: '25%'
    },
    {
      text: 'Time Taken',
      value: 'timeTaken',
      align: 'center',
      width: '25%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournament = new Tournament(
        JSON.parse(this.$route.params.tournament)
      );
      if (this.tournament.id)
        this.tournamentResults = await RemoteServices.getTournamentResults(
          this.tournament.id
        );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async showResults() {
    try {
      if (this.tournament?.id) {
        let quiz = await RemoteServices.getTournamentSolvedQuiz(
          this.tournament.id
        );
        let statementManager: StatementManager = StatementManager.getInstance;
        statementManager.correctAnswers = quiz.correctAnswers;
        statementManager.statementQuiz = quiz.statementQuiz;
        await this.$router.push({ name: 'quiz-results' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getNumberColor(tournamentResult: TournamentResult) {
    let place =
      ((this.tournamentResults.indexOf(tournamentResult) + 1) /
        this.tournamentResults.length) *
      100;

    if (place < 25) return 'red';
    else if (place < 50) return 'orange';
    else if (place < 75) return 'lime';
    else return 'green';
  }

  place(tournamentResult: TournamentResult) {
    return this.tournamentResults.indexOf(tournamentResult) + 1;
  }
}
</script>

<style lang="scss" scoped>
.gold {
  background: rgb(233, 211, 98);
  background: linear-gradient(
    62deg,
    rgba(233, 211, 98, 1) 0%,
    rgba(51, 51, 51, 0.1) 100%
  );
}

.large-table {
  tr {
    height: 100px;

    td {
      font-size: 1.1rem;
    }
  }
}

.silver {
  background: rgb(169, 169, 169);
  background: linear-gradient(
    62deg,
    rgba(169, 169, 169, 1) 0%,
    rgba(51, 51, 51, 0.1) 100%
  );
}
</style>
