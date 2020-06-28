<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="stats"
      :search="search"
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-row>
            <v-col>
              <v-text-field
                v-model="search"
                append-icon="search"
                label="Search"
                data-cy="Search"
                class="mx-2"
              />
            </v-col>
            <v-col class="subtitle-container">
              <v-icon class="pl-0 icon-lock" small>fa-lock</v-icon>
              <span class="ml-1 subtitle">- Information not conceded by the student</span>
            </v-col>
          </v-row>
        </v-card-title>
      </template>

      <template v-slot:item.totalClarificationRequests="{ item }">
        <v-icon
          v-if="item.privateClarificationStats"
          class="pl-0 icon-lock"
          data-cy="iconPrivateClarification"
          small
        >fa-lock</v-icon>
        <span v-else>{{item.totalClarificationRequests}}</span>
      </template>
      <template v-slot:item.totalPublicClarificationRequests="{ item }">
        <v-icon v-if="item.privateClarificationStats" class="pl-0 icon-lock" small>fa-lock</v-icon>
        <span v-else>{{item.totalPublicClarificationRequests}}</span>
      </template>

      <template v-slot:item.totalNumberSuggestions="{ item }">
        <v-icon
          v-if="item.privateSuggestionStats"
          class="pl-0 icon-lock"
          data-cy="iconPrivateSuggestion"
          small
        >fa-lock</v-icon>
        <span v-else>{{item.totalNumberSuggestions}}</span>
      </template>

      <template v-slot:item.totalNumberSuggestionsAccepted="{ item }">
        <v-icon v-if="item.privateSuggestionStats" class="pl-0 icon-lock" small>fa-lock</v-icon>
        <span v-else>{{item.totalNumberSuggestionsAccepted}}</span>
      </template>

      <template v-slot:item.totalNumberCreatedTournaments="{ item }">
        <v-icon
          v-if="item.privateTournamentsStats"
          class="pl-0 icon-lock"
          data-cy="iconPrivateTournament"
          small
        >fa-lock</v-icon>
        <span v-else>{{item.totalNumberCreatedTournaments}}</span>
      </template>

      <template v-slot:item.totalNumberEnrolledTournaments="{ item }">
        <v-icon v-if="item.privateTournamentsStats" class="pl-0 icon-lock" small>fa-lock</v-icon>
        <span v-else>{{item.totalNumberEnrolledTournaments}}</span>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { ClarificationRequest } from '../../models/discussion/ClarificationRequest';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import StudentStats from '@/models/statement/StudentStats';
import Image from '@/models/management/Image';
import StudentsView from '@/views/teacher/students/StudentsView.vue';

@Component({
  components: { StudentsView }
})
export default class ListStudentStatsView extends Vue {
  @Prop({ type: Array, required: true }) readonly stats!: StudentStats[];
  search: string = '';
  tabs = null;

  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '60%' },
    {
      text: 'Clarification Requests',
      value: 'totalClarificationRequests',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Public Clarification Requests',
      value: 'totalPublicClarificationRequests',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Question Suggestions',
      value: 'totalNumberSuggestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Accepted Question Suggestions',
      value: 'totalNumberSuggestionsAccepted',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Created Tournaments',
      value: 'totalNumberCreatedTournaments',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Enrolled Tournaments',
      value: 'totalNumberEnrolledTournaments',
      align: 'center',
      width: '10%'
    }
  ];
}
</script>

<style scoped>
.subtitle {
  font-size: 0.875rem;
  height: 48px;
  color: rgb(0, 0, 0, 0.6);
  font-family: Roboto, sans-serif;
  line-height: 1.5;
}

.subtitle-container {
  text-align: right;
  margin-top: 10px;
}
</style>
