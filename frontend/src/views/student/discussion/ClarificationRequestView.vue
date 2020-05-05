<template>
    <div>
        <clarification-request :clarification-request="clarificationRequest" />
        <clarification-request-answer
                v-for="clarificationRequestAnswer in clarificationRequestAnswers"
                :clarification-request-answer="clarificationRequestAnswer"
                :key="clarificationRequestAnswer.id"
        />
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';
  import ClarificationRequestAnswerComponent from "@/components/discussion/ClarificationRequestAnswerComponent.vue";
  import RemoteServices from '@/services/RemoteServices';
  import { ClarificationRequestAnswer } from '@/models/discussion/ClarificationRequestAnswer';
  import CreateClarificationRequestAnswerDialog
    from '@/views/teacher/discussion/CreateClarificationRequestAnswerDialog.vue';

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

    async created() {
      this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
      if (this.clarificationRequest.id !== null) {
        await this.$store.dispatch('loading');
        try {
          this.clarificationRequestAnswers = await RemoteServices.getClarificationRequestAnswers(this.clarificationRequest.id);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
        await this.$store.dispatch('clearLoading');
      }
    }

    newClarificationRequestAnswer() {
      this.currentClarificationRequestAnswer = new ClarificationRequestAnswer();
      this.createClarificationRequestAnswerDialog = true;
    }

    async onCreateClarificationRequestAnswer(clarificationRequestAnswer: ClarificationRequestAnswer) {
      this.createClarificationRequestAnswerDialog = false;
      this.currentClarificationRequestAnswer = null;
    }

    onCloseDialog() {
      this.createClarificationRequestAnswerDialog = false;
      this.currentClarificationRequestAnswer = null;
    }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
