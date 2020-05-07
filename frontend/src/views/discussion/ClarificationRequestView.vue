<template>
    <div class="container" :key="this.update">
        <clarification-request :clarification-request="clarificationRequest" />
        <clarification-request-answer
                v-for="clarificationRequestAnswer in clarificationRequestAnswers" :clarification-request-answer="clarificationRequestAnswer"
                :key="clarificationRequestAnswer.id"
        />
        <v-container>
            <v-btn v-if="this.clarificationRequest.status !== 'CLOSED'" class="mr-5" color="primary" dark @click="newClarificationRequestAnswer" data-cy="answerButton">
                <v-icon left>mdi-reply</v-icon> Reply
            </v-btn>
            <v-btn v-else class="mr-5" disabled data-cy="answerButtonDisabled">
                <v-icon left>mdi-reply</v-icon> Reply
            </v-btn>
            <v-btn v-if="this.$store.getters.isStudent && this.clarificationRequest.status !== 'CLOSED'" color="primary" dark @click="closeClarificationRequest" data-cy="closeButton">
                <v-icon left>mdi-close</v-icon> Close Clarification
            </v-btn>
            <v-btn v-else-if="this.$store.getters.isStudent" disabled data-cy="closeButtonDisabled">
                <v-icon left>mdi-close</v-icon> Close Clarification
            </v-btn>
        </v-container>
        <create-clarification-request-answer-dialog
                v-if="currentClarificationRequestAnswer"
                v-model="createClarificationRequestAnswerDialog"
                :clarification-request-answer="currentClarificationRequestAnswer"
                :clarification-request="clarificationRequest"
                v-on:new-clarification-request-answer="onCreateClarificationRequestAnswer"
                v-on:close-dialog="onCloseDialog"
        />
    </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import CreateClarificationRequestAnswerDialog
    from '@/views/discussion/CreateClarificationRequestAnswerDialog.vue';
  import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';
  import {ClarificationRequestAnswer} from '@/models/discussion/ClarificationRequestAnswer';
  import ClarificationRequestAnswerComponent from '@/components/discussion/ClarificationRequestAnswerComponent.vue';
  import RemoteServices from '@/services/RemoteServices';

  @Component({
    components: {
      'create-clarification-request-answer-dialog': CreateClarificationRequestAnswerDialog,
      'clarification-request': ClarificationRequestComponent,
      'clarification-request-answer': ClarificationRequestAnswerComponent
    }
  })

  export default class ClarificationRequestView extends Vue {
    clarificationRequest!: ClarificationRequest;
    currentClarificationRequestAnswer: ClarificationRequestAnswer | null = null;
    createClarificationRequestAnswerDialog: boolean = false;
    clarificationRequestAnswers: ClarificationRequestAnswer[] = [];
    update: number = 1;

    headers: object = [
      { text: 'Clarification', value: 'content', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' }
    ];

    async created() {
      this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
      this.getClarificationRequestAnswers();
    }

    newClarificationRequestAnswer() {
      this.currentClarificationRequestAnswer = new ClarificationRequestAnswer();
      this.createClarificationRequestAnswerDialog = true;
    }

    onCreateClarificationRequestAnswer() {
      this.createClarificationRequestAnswerDialog = false;
      this.currentClarificationRequestAnswer = null;
      if (this.$store.getters.isTeacher)
        this.clarificationRequest.status = 'ANSWERED';
      if (this.$store.getters.isStudent)
        this.clarificationRequest.status = 'OPEN';
      this.getClarificationRequestAnswers();
    }

    onCloseDialog() {
      this.createClarificationRequestAnswerDialog = false;
      this.currentClarificationRequestAnswer = null;
    }

    async closeClarificationRequest() {
      if (this.clarificationRequest.id !== null) {
        await this.$store.dispatch('loading');
        try {
          this.clarificationRequest = await RemoteServices.closeClarificationRequest(this.clarificationRequest.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
      }

      this.update++;
    }

    async getClarificationRequestAnswers() {
      if (this.clarificationRequest.id !== null) {
        await this.$store.dispatch('loading');
        try {
          this.clarificationRequestAnswers = await RemoteServices.getClarificationRequestAnswers(this.clarificationRequest.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
      }

      this.update++;
    }
  }
</script>

<style lang="scss" scoped>
</style>