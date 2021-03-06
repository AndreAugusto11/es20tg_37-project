<template>
  <div>
    <create-tournament-view
      v-if="currentTournament"
      v-model="createTournamentDialog"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
      :tournament="currentTournament"
    />
    <v-card v-else class="table">
      <v-data-table
        :headers="headers"
        :items="allTournaments ? tournaments : myTournaments"
        :search="search"
        multi-sort
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        :key="update"
      >
        <template v-slot:top>
          <v-card-title>
            <span class="headline">
              Tournaments
            </span>
          </v-card-title>

          <v-row align="left">
            <v-col class="d-flex" cols="12" sm="4">
              <v-text-field
                v-model="search"
                append-icon="search"
                label="Search"
                class="mx-2"
              />
            </v-col>
            <v-col class="mt-3 d-flex" cols="12" sm="4">
              <v-select
                :items="tournamentsFilters"
                label="Tournaments"
                dense
                outlined
                class="ml-5"
                style="max-width: 300px;"
                value="All Tournaments"
                v-on:change="changeTournamentsFilter"
              />
            </v-col>
            <v-col class="mt-3" cols="12" sm="4" align="right">
              <v-btn
                color="primary"
                dark
                @click="newTournament"
                data-cy="createButton"
              >
                New Tournament
              </v-btn>
            </v-col>
          </v-row>
        </template>

        <template v-slot:item.topics="{ item }">
          <div
            :key="topicConjunction.id"
            v-for="topicConjunction in item.topicConjunctions"
          >
            <v-chip
              :key="topic.id"
              v-for="topic in topicConjunction.topics"
              style="margin: 5px;"
            >
              {{ topic.name }}
            </v-chip>
          </div>
        </template>

        <template v-slot:item.numberOfEnrolls="{ item }">
          <v-chip>
            {{ item.enrolledStudentsNames.length }}
          </v-chip>
        </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip v-if="canEnroll(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                color="blue"
                dark
                v-on="on"
                @click="enrollTournament(item)"
                data-cy="enrollTournament"
              >
                mdi-location-enter
              </v-icon>
            </template>
            <span>Enroll Tournament</span>
          </v-tooltip>

          <v-tooltip v-else bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                v-on="on"
                disabled
                data-cy="disabledEnrollTournament"
              >
                mdi-location-enter
              </v-icon>
            </template>
            <span>Enroll Tournament</span>
          </v-tooltip>

          <v-tooltip v-if="canAnswer(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                color="blue"
                dark
                v-on="on"
                @click="answerQuiz(item)"
                data-cy="answerTournament"
              >
                mdi-file-move
              </v-icon>
            </template>
            <span>Answer Quiz</span>
          </v-tooltip>

          <v-tooltip v-else bottom>
            <template v-slot:activator="{ on }">
              <v-icon x-large class="mr-2" v-on="on" disabled>
                mdi-file-move
              </v-icon>
            </template>
            <span>Answer Quiz</span>
          </v-tooltip>

          <v-tooltip
            v-if="myTournaments.includes(item) && item.status === 'ENROLLING'"
            bottom
          >
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                color="red"
                dark
                v-on="on"
                @click="cancelTournament(item)"
                data-cy="cancelTournament"
              >
                mdi-cancel
              </v-icon>
            </template>
            <span>Cancel Tournament</span>
          </v-tooltip>

          <v-tooltip v-else bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                v-on="on"
                disabled
                data-cy="disabledCancelTournament"
              >
                mdi-cancel
              </v-icon>
            </template>
            <span>Cancel Tournament</span>
          </v-tooltip>

          <v-tooltip v-if="canOpenQuiz(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                color="blue"
                dark
                v-on="on"
                @click="openTournamentResults(item)"
                data-cy="openTournamentResults"
              >
                mdi-trophy
              </v-icon>
            </template>
            <span>See Tournament Results</span>
          </v-tooltip>

          <v-tooltip v-if="canOpenQuiz(item)" bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                x-large
                class="mr-2"
                color="blue"
                dark
                v-on="on"
                @click="openTournamentQuiz(item.id)"
                data-cy="openTournamentQuiz"
              >
                mdi-file-document-outline
              </v-icon>
            </template>
            <span>See Quiz Results</span>
          </v-tooltip>
        </template>

        <template v-slot:item.status="{ item }">
          <v-chip :color="getStatusColor(item.status)" small>
            <span>{{ item.status }}</span>
          </v-chip>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';
import CreateTournamentDialog from '@/views/student/tournaments/CreateTournamentView.vue';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementManager from '@/models/statement/StatementManager';
import CreateTournamentView from '@/views/student/tournaments/CreateTournamentView.vue';

@Component({
  components: {
    'create-tournament-view': CreateTournamentView
  }
})
export default class ListTournamentsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  tournaments: Tournament[] = [];
  myTournaments: Tournament[] = [];
  allTournaments: boolean = true;
  currentTournament: Tournament | null = null;
  createTournamentDialog: boolean = false;
  search: string = '';
  tournamentsFilters: string[] = ['All Tournaments', 'My Tournaments'];
  update: number = 0;
  headers: object = [
    {
      text: 'Creator',
      value: 'creatorName',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Title',
      value: 'title',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Enrolls',
      value: 'numberOfEnrolls',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      width: '20%'
    },
    {
      text: 'Available Date',
      value: 'availableDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Conclusion Date',
      value: 'conclusionDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Results Date',
      value: 'resultsDate',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'right',
      sortable: false,
      width: '15%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.statementManager.reset();
      this.tournaments = await RemoteServices.getTournaments();
      this.myTournaments = this.tournaments.filter(
        tournament =>
          tournament.creatorName === this.$store.getters.getUser.name
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async newTournament() {
    this.currentTournament = new Tournament();
    this.createTournamentDialog = true;
  }

  async onCreateTournament(tournament: Tournament) {
    this.tournaments.unshift(tournament);
    this.myTournaments.unshift(tournament);
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  async onCloseDialog() {
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  async cancelTournament(tournamentToCancel: Tournament) {
    if (
      confirm('Are you sure you want to cancel?\nThis action in non-reversible')
    ) {
      try {
        if (tournamentToCancel.id) {
          await RemoteServices.cancelTournament(tournamentToCancel.id);
          this.tournaments[
            this.tournaments.indexOf(tournamentToCancel)
          ].status = 'CANCELLED';
          this.myTournaments[
            this.myTournaments.indexOf(tournamentToCancel)
          ].status = 'CANCELLED';
          this.update += 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async enrollTournament(tournamentToEnroll: Tournament) {
    if (confirm('Are you sure you want to enroll?')) {
      try {
        if (tournamentToEnroll.id) {
          this.tournaments[
            this.tournaments.indexOf(tournamentToEnroll)
          ] = await RemoteServices.enrollTournament(tournamentToEnroll.id);
          this.update += 1;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async answerQuiz(tournamentToAnswer: Tournament) {
    try {
      this.statementManager.reset();
      if (tournamentToAnswer.id) {
        await this.statementManager.setTournamentQuiz(tournamentToAnswer.id);
        await this.$router.push({ name: 'solve-quiz' });
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string): string {
    if (status === 'ENROLLING') return 'green';
    else if (status === 'ONGOING') return 'orange';
    else return 'red';
  }

  changeTournamentsFilter(value: string) {
    this.allTournaments = value === 'All Tournaments';
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  canEnroll(tournament: Tournament): boolean {
    return (
      !tournament.enrolledStudentsNames.includes(
        this.$store.getters.getUser.name
      ) && tournament.status === 'ENROLLING'
    );
  }

  canAnswer(tournament: Tournament): boolean {
    return (
      tournament.enrolledStudentsNames.includes(
        this.$store.getters.getUser.name
      ) && tournament.status === 'ONGOING'
    );
  }

  canOpenQuiz(tournament: Tournament): boolean {
    return (
      tournament.enrolledStudentsNames.includes(
        this.$store.getters.getUser.name
      ) && tournament.status !== 'ENROLLING'
    );
  }

  async openTournamentQuiz(tournamentId: number) {
    try {
      let quiz = await RemoteServices.getTournamentSolvedQuiz(tournamentId);
      this.statementManager.reset();
      this.statementManager.correctAnswers = quiz.correctAnswers;
      this.statementManager.statementQuiz = quiz.statementQuiz;
      await this.$router.push({ name: 'quiz-results' });
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async openTournamentResults(value: Tournament) {
    await this.$router.push({
      name: 'tournament-results',
      params: { tournament: JSON.stringify(value) }
    });
  }
}
</script>

<style lang="scss" scoped>
.pos-text {
  text-align: left !important;
}
</style>
