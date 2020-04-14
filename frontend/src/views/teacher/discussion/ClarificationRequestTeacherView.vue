<template>
    <div class="container">
        <clarification-request :clarification-request="clarificationRequest" />
        <v-container>
            <v-btn color="primary" dark @click="newClarificationRequestAnswer" data-cy="createButton">
                Answer
            </v-btn>
        </v-container>
    </div>
</template>

<script lang="ts">
    import { Component, Vue } from 'vue-property-decorator';
    import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
    import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
    import CreateClarificationRequestAnswerDialog
        from "@/views/teacher/discussion/CreateClarificationRequestAnswerDialog.vue";
    import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';

    @Component({
      components: {
        'create-clarification-request-answer-dialog': CreateClarificationRequestAnswerDialog,
        'clarification-request': ClarificationRequestComponent
      }
    })
    export default class ClarificationRequestTeacherView extends Vue {
        clarificationRequest!: ClarificationRequest;

        headers: object = [
            { text: 'Clarification', value: 'content', align: 'left' },
            { text: 'Status', value: 'status', align: 'center' }
        ];

        created() {
            this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
        }

        newClarificationRequestAnswer() {

        }
    }
</script>

<style lang="scss" scoped>
</style>