<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :custom-filter="customFilter"
      :items="questionSuggestions"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <!-- Header if Student -->
      <template v-if="this.$store.getters.isStudent" v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn color="primary" dark @click="newQuestionSuggestion">
            New Suggestion
          </v-btn>
        </v-card-title>
      </template>

      <!-- Header if Teacher or Admin -->
      <template v-else v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDown(item.content, null)"
      /></template>

      <!-- Actions if Teacher -->
      <template v-if="this.$store.getters.isTeacher" v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSuggestion(item)"
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
              @click="showJustificationDialog(item)"
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

      <!-- Actions if Admin -->
      <template v-else-if="this.$store.getters.isAdmin" v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSuggestion(item)"
              data-cy="showButton"
              >visibility</v-icon
            >
          </template>
          <span>Show Suggestion</span>
        </v-tooltip>

        <v-tooltip bottom v-if="item.status === 'REJECTED' || item.status === 'ACCEPTED'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="remove(item.id)"
              data-cy="removeButton"
              color="red"
            >mdi-trash-can</v-icon>
          </template>
          <span>Remove Suggestion</span>
        </v-tooltip>
          <template v-else>
            <v-icon disabled class="mr-2">mdi-trash-can</v-icon>
          </template>
      </template>

      <!-- Actions if Student -->
      <template v-else v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSuggestion(item)"
              data-cy="showSuggestion"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
        <v-tooltip bottom v-if="item.status === 'REJECTED'">
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="editRejectedQuestionSuggestion(item)"
              data-cy="updateRejectedQuestion"
              >edit</v-icon
            >
          </template>
          <span>Edit Question</span>
        </v-tooltip>
          <template v-else>
            <v-icon
              class="mr-2"
              disabled
              >edit</v-icon
            >
          </template>
      </template>
    </v-data-table>

    <edit-questionSuggestion-dialog
      v-if="currentQuestionSuggestion"
      v-model="editQuestionSuggestionDialog"
      :questionSuggestion="currentQuestionSuggestion"
      v-on:save-questionSuggestion="saveQuestionSuggestion"
      v-on:close-dialog="closeQuestionSuggestionDialog"
    />

    <justification-dialog
      v-if="currentSuggestion"
      :dialog="rejectionDialog"
      :questionSuggestion="currentSuggestion"
      v-on:reject-suggestion="rejectQuestionSuggestion(currentSuggestion.id, $event)"
      v-on:close-dialog="onCloseJustificationDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import EditQuestionSuggestionDialog from '@/views/student/questionSuggestion/EditQuestionSuggestionDialog.vue';
import JustificationDialog from '@/views/teacher/questionSuggestions/JustificationDialog.vue';
import Justification from '@/models/management/Justification';

@Component({
  components: {
    'edit-questionSuggestion-dialog': EditQuestionSuggestionDialog,
    'justification-dialog': JustificationDialog
  }
})
export default class QuestionSuggestionView extends Vue {
  questionSuggestions: QuestionSuggestion[] = [];
  currentQuestionSuggestion: QuestionSuggestion | null = null;
  editQuestionSuggestionDialog: boolean = false;
  currentSuggestion: QuestionSuggestion | null = null;
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

      if (this.$store.getters.isTeacher)
        [this.questionSuggestions] = await Promise.all([
          RemoteServices.getAllQuestionSuggestions()
        ]);
      
      else if (this.$store.getters.isAdmin)
        [this.questionSuggestions] = await Promise.all([
          RemoteServices.getAllQuestionSuggestionsAdmin()
        ]);
      
      else if (this.$store.getters.isStudent)
        [this.questionSuggestions] = await Promise.all([
          RemoteServices.getQuestionSuggestions()
        ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newQuestionSuggestion() {
    this.currentQuestionSuggestion = new QuestionSuggestion();
    this.editQuestionSuggestionDialog = true;
  }

  editRejectedQuestionSuggestion(questionSuggestion: QuestionSuggestion, e?: Event) {
    if (e)
      e.preventDefault();

    this.currentQuestionSuggestion = questionSuggestion;
    this.editQuestionSuggestionDialog = true;
  }

  customFilter(
    value: string,
    search: string,
    questionSuggestion: QuestionSuggestion
  ) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(questionSuggestion)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'PENDING') return 'orange';
    else return 'green';
  }

  saveQuestionSuggestion(questionSuggestion: QuestionSuggestion) {
    this.questionSuggestions = this.questionSuggestions.filter(
      q => q.id !== questionSuggestion.id
    );
    this.questionSuggestions.unshift(questionSuggestion);
    this.editQuestionSuggestionDialog = false;
    this.currentQuestionSuggestion = null;
  }

  async showQuestionSuggestion(questionSuggestion: QuestionSuggestion) {

    if (this.$store.getters.isTeacher)
      await this.$router.push({ name: 'suggestionTeacher', params: { questionSuggestion: JSON.stringify(questionSuggestion) } });

    else if (this.$store.getters.isAdmin)
      await this.$router.push({ name: 'suggestionAdmin', params: { questionSuggestion: JSON.stringify(questionSuggestion) } });
    
    else if (this.$store.getters.isStudent)
      await this.$router.push({ name: 'suggestionStudent', params: { questionSuggestion: JSON.stringify(questionSuggestion) } });
  }

  async remove(suggestionId: number) {
    try {
      if (confirm('Are you sure you want to remove this suggestion?')) {
        await RemoteServices.removeQuestionSuggestion(suggestionId);
        this.questionSuggestions = this.questionSuggestions.filter(
          suggestion => suggestion.id !== suggestionId
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async rejectQuestionSuggestion(suggestionId: number, justification: Justification) {
    try {
      let suggestion = this.questionSuggestions.find(suggestion => suggestion.id === suggestionId);
      if (suggestion && suggestion.justificationDto) {
        suggestion.status = 'REJECTED';
        suggestion.justificationDto.content = justification.content;
        suggestion.justificationDto.image = justification.image;
      }
      
      this.onCloseJustificationDialog();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  showJustificationDialog(suggestion: QuestionSuggestion) {
    this.currentSuggestion = suggestion;
    this.rejectionDialog = true;
  }

  onCloseJustificationDialog() {
    this.rejectionDialog = false;
    this.currentSuggestion = null;
  }

  async accepted(suggestionId: number) {
    try {
      await RemoteServices.acceptQuestionSuggestion(suggestionId);
      let suggestion = this.questionSuggestions.find(
        suggestion => suggestion.id === suggestionId
      );
      if (suggestion) {
        suggestion.status = 'ACCEPTED';
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  closeQuestionSuggestionDialog() {
    this.currentQuestionSuggestion = null;
  }
}
</script>

<style lang="scss" scoped>
.question-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 200px !important;
  }
}
.option-textarea {
  text-align: left;

  .CodeMirror,
  .CodeMirror-scroll {
    min-height: 100px !important;
  }
}
</style>
