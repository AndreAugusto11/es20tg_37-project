<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
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
        </v-card-title>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

    </v-data-table>
  </v-card>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import QuestionSuggestion from '@/models/management/QuestionSuggestion';
  import RemoteServices from '@/services/RemoteServices';

  @Component
  export default class SuggestionsTView extends Vue {
    questionSuggestions: QuestionSuggestion[] = [];
    search: string = '';
    headers: object = [
      { text: 'Title', value: 'questionDto.title', align: 'center' },
      { text: 'Content', value: 'questionDto.content', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' },
      { text: 'Creation Date', value: 'creationDate', align: 'center' },
      { text: 'Actions', value: 'action', align: 'center', sortable: false }
    ];

    async created() {
      await this.$store.dispatch('loading');
      try {
        [this.questionSuggestions] = await Promise.all([
          RemoteServices.getAllQuestionSuggestions()
        ]);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    getStatusColor(status: string) {
      if (status === 'REJECTED') return 'red';
      else if (status === 'PENDING') return 'orange';
      else return 'green';
    }
  }


</script>



<style scoped></style>