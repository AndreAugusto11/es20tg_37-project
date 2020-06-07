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
          <v-text-field v-model="search" append-icon="search" label="Search" class="mx-2" />
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
              @click="showSuggestion(item)"
              data-cy="showButton"
            >visibility</v-icon>
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
        <v-tooltip bottom v-else>
          <template v-slot:activator="{ on }">
            <v-icon class="mr-2" v-on="on" data-cy="disabledRemoveButton">mdi-trash-can</v-icon>
          </template>
          <span>Remove Suggestion</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class AdminQuestionSuggestionsView extends Vue {
  suggestions: QuestionSuggestion[] = [];
  search: string = '';

  headers: object = [
    { text: 'Title', value: 'questionDto.title', align: 'left' },
    {
      text: 'Content',
      value: 'questionDto.content',
      align: 'left',
      width: '45%'
    },
    { text: 'Creation Date', value: 'creationDate', align: 'center' },
    { text: 'Status', value: 'status', align: 'center' },
    { text: 'Actions', value: 'action', align: 'center', sortable: false }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.suggestions] = await Promise.all([
        RemoteServices.getAllQuestionSuggestionsAdmin()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async remove(suggestionId: number) {
    try {
      if (confirm('Are you sure you want to remove this suggestion?')) {
        await RemoteServices.removeQuestionSuggestion(suggestionId);
        this.suggestions = this.suggestions.filter(
          suggestion => suggestion.id !== suggestionId
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string) {
    if (status === 'PENDING') return 'orange';
    else if (status === 'ACCEPTED') return 'green';
    else return 'red';
  }

  async showSuggestion(questionSuggestion: QuestionSuggestion) {
    await this.$router.push({
      name: 'suggestionAdmin',
      params: { questionSuggestion: JSON.stringify(questionSuggestion) }
    });
  }
}
</script>

<style scoped></style>
