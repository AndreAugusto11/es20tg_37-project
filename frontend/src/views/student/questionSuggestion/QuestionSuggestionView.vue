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
      <template v-slot:top>
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

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDown(item.content, null)"
      /></template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              class="mr-2"
              v-on="on"
              @click="showQuestionSuggestion
          (item)"
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
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import EditQuestionSuggestionDialog from '@/views/student/questionSuggestion/EditQuestionSuggestionDialog.vue';

@Component({
  components: {
    'edit-questionSuggestion-dialog': EditQuestionSuggestionDialog
  }
})
export default class QuestionSuggestionView extends Vue {
  questionSuggestions: QuestionSuggestion[] = [];
  currentQuestionSuggestion: QuestionSuggestion | null = null;
  editQuestionSuggestionDialog: boolean = false;
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'left' },
    { text: 'Content', value: 'questionDto.content', align: 'left' },
    { text: 'Creation Date', value: 'creationDate', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' }, 
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
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
    await this.$router.push({ name: 'test', params: { questionSuggestion: JSON.stringify(questionSuggestion) } });
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
