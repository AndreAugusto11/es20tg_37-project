<template>
  <div class="container">
    <v-card>
      <v-card dark color="primary">
        <v-card-title class="text-center justify-center py-6">
          <h1 class="font-weight-bold display-3 basil--text">Statistics</h1>
        </v-card-title>
        <v-tabs v-model="tabs" centered dark background-color="primary">
          <v-tab>Quizzes</v-tab>
          <v-tab data-cy="otherStats">Other</v-tab>
        </v-tabs>
      </v-card>

      <v-tabs-items v-model="tabs">
        <v-tab-item>
          <StudentsView></StudentsView>  
        </v-tab-item>
        <v-tab-item>
          <ListStudentStatsView :stats="stats" />
        </v-tab-item>
      </v-tabs-items>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ClarificationRequest } from '../../models/discussion/ClarificationRequest';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import StudentStats from '@/models/statement/StudentStats';
import Image from '@/models/management/Image';
import StudentsView from '@/views/teacher/students/StudentsView.vue';
import ListStudentStatsView from '@/views/teacher/ListStudentStatsView.vue';

@Component({
    components: { StudentsView, ListStudentStatsView }
})
export default class TeacherStatsView extends Vue {
  stats: StudentStats[] = [];
  search: string = '';
  tabs = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getSimplifiedStudentsStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>