<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="suggestions"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
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
        </v-card-title>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showSuggestionDialog(item)"
              data-cy="showButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Suggestion</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.status === 'PENDING'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="accepted(item.id)"
              data-cy="acceptButton"
              >mdi-check</v-icon
            >
          </template>
          <span>Accept Suggestion</span>
        </v-tooltip>
        <template v-else>
          <v-icon
            class="mr-2"
            disabled
            >mdi-check</v-icon
          >
        </template>
        <v-tooltip bottom v-if="item.status === 'PENDING'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showRejectionDialog(item)"
              data-cy="rejectButton"
              >mdi-close</v-icon
            >
          </template>
          <span>Reject Suggestion</span>
        </v-tooltip>
        <template v-else>
          <v-icon
            class="mr-2"
            disabled
            >mdi-close</v-icon
          >
        </template>
      </template>
    </v-data-table>

    <show-justification-dialog
      v-if="currentSuggestion"
      :dialog="suggestionDialog"
      :questionSuggestion="currentSuggestion"
      v-on:reject-suggestion="rejected(currentSuggestion.id, $event)"
      v-on:close-dialog="onCloseSuggestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import RemoteServices from '@/services/RemoteServices';
import ShowJustificationDialog from '@/views/teacher/questionSuggestions/ShowJustificationDialog.vue';
import Justification from '@/models/management/Justification';
import Image from '@/models/management/Image';

@Component({
  components: {
    'show-justification-dialog': ShowJustificationDialog
  }
})
export default class SuggestionsTView extends Vue {
  suggestions: QuestionSuggestion[] = [];
  currentSuggestion: QuestionSuggestion | null = null;
  suggestionDialog: boolean = false;
  rejectionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'left' },
    { text: 'Content', value: 'questionDto.content', align: 'left', width: '45%' },
    { text: 'Creation Date', value: 'creationDate', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' }, 
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.suggestions] = await Promise.all([
        RemoteServices.getAllQuestionSuggestions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async rejected(suggestionId: number, justification: Justification) {
    try {
      let suggestion = this.suggestions.find(suggestion => suggestion.id === suggestionId);
      if (suggestion && suggestion.justificationDto) {
        suggestion.status = 'REJECTED';
        suggestion.justificationDto.content = justification.content;
        suggestion.justificationDto.image = justification.image;
      }
      
      this.onCloseSuggestionDialog();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string) {
    if (status === 'PENDING') return 'orange';
    else if (status === 'ACCEPTED') return 'green';
    else return 'red';
  }

  async showSuggestionDialog(questionSuggestion: QuestionSuggestion) {
    this.currentSuggestion = questionSuggestion;
    this.rejectionDialog = false;
    this.suggestionDialog = true;

    await this.$router.push({ name: 'suggestionTeacher', params: { questionSuggestion: JSON.stringify(questionSuggestion) } });
  }

  showRejectionDialog(suggestion: QuestionSuggestion) {
    this.currentSuggestion = suggestion;
    this.rejectionDialog = true;
    this.suggestionDialog = true;
  }

  onCloseSuggestionDialog() {
    this.suggestionDialog = false;
    this.rejectionDialog = false;
    this.currentSuggestion = null;
  }
}
</script>

<style scoped></style>
