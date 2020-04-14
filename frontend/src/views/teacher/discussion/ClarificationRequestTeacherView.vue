<template>
    <div class="container">
        <clarification-request :clarification-request="clarificationRequest" />
        <v-container>
            <v-btn color="primary" dark @click="newClarificationRequestAnswer" data-cy="createButton">
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
        from "@/views/teacher/discussion/CreateClarificationRequestAnswerDialog.vue";
    import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';
    import {ClarificationRequestAnswer} from "@/models/discussion/ClarificationRequestAnswer";

    @Component({
      components: {
        'create-clarification-request-answer-dialog': CreateClarificationRequestAnswerDialog,
        'clarification-request': ClarificationRequestComponent
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

        async onCreateClarificationRequestAnswer() {
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