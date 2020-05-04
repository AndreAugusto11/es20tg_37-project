<template>
    <div class="container">
        <clarification-request :clarification-request="clarificationRequest" />
        <clarification-request-answer
                v-for="clarificationRequestAnswer in clarificationRequestAnswers" :clarification-request-answer="clarificationRequestAnswer"
                :key="clarificationRequestAnswer.id"
        />
        <v-container>
            <v-btn v-if="this.clarificationRequest.status !== 'CLOSED'" color="primary" dark @click="newClarificationRequestAnswer" data-cy="answerButton">
                Answer
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
        from '@/views/teacher/discussion/CreateClarificationRequestAnswerDialog.vue';
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

    export default class ClarificationRequestTeacherView extends Vue {
        clarificationRequest!: ClarificationRequest;
        currentClarificationRequestAnswer: ClarificationRequestAnswer | null = null;
        createClarificationRequestAnswerDialog: boolean = false;
        clarificationRequestAnswers: ClarificationRequestAnswer[] = [];

        headers: object = [
            { text: 'Clarification', value: 'content', align: 'left' },
            { text: 'Status', value: 'status', align: 'center' }
        ];

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
</style>