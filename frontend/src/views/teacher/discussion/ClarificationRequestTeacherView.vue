<template>
    <div class="container" :key="clarificationRequest.clarificationRequestAnswerDto.id">
        <clarification-request :clarification-request="clarificationRequest" />
        <clarification-request-answer :clarification-request="clarificationRequest" />
        <v-container>
            <v-btn v-if="!this.clarificationRequest.clarificationRequestAnswerDto.content" color="primary" dark @click="newClarificationRequestAnswer" data-cy="createButton">
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

        headers: object = [
            { text: 'Clarification', value: 'content', align: 'left' },
            { text: 'Status', value: 'status', align: 'center' }
        ];

        created() {
            this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
        }

        newClarificationRequestAnswer() {
            this.currentClarificationRequestAnswer = new ClarificationRequestAnswer();
            this.createClarificationRequestAnswerDialog = true;
        }

        async onCreateClarificationRequestAnswer(clarificationRequestAnswer: ClarificationRequestAnswer) {
            this.clarificationRequest.clarificationRequestAnswerDto = clarificationRequestAnswer;
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