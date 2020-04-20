<template>
    <v-dialog
            :value="dialog"
            @input="$emit('close-dialog')"
            @keydown.esc="$emit('close-dialog')"
            max-width="50%"
            max-height="80%"
    >
        <v-card>
            <v-card-title>
                <span class="headline">
                  New Clarification Request
                </span>
            </v-card-title>

            <v-card-text class="text-left" v-if="createClarificationRequest">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md8>
                            <v-textarea
                                    outline
                                    rows="5"
                                    v-model="createClarificationRequest.content"
                                    label="Content"
                                    data-cy="Content"
                            ></v-textarea>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn
                        color="blue darken-1"
                        @click="$emit('close-dialog')"
                        data-cy="cancelButton"
                >
                    Cancel
                </v-btn>
                <v-btn color="blue darken-1" @click="saveClarificationRequest" data-cy="saveButton">
                    Send
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
  import { Component, Model, Prop, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import StatementAnswer from '@/models/statement/StatementAnswer';

  @Component
  export default class CreateClarificationRequestDialog extends Vue {
    @Model('dialog', Boolean) dialog!: boolean;
    @Prop({ type: ClarificationRequest, required: true }) readonly clarificationRequest!: ClarificationRequest;
    @Prop({ type: StatementAnswer, required: true }) readonly answer!: StatementAnswer;

    createClarificationRequest!: ClarificationRequest;

    created() {
      this.createClarificationRequest = new ClarificationRequest(this.clarificationRequest);
    }

    async saveClarificationRequest() {
      if (
        this.createClarificationRequest &&
        (!this.createClarificationRequest.content)
      ) {
        await this.$store.dispatch(
          'error',
          'Clarification request must have content'
        );
        return;
      }

      if (this.createClarificationRequest && this.answer.questionAnswerId) {
        try {
          this.createClarificationRequest.name = this.$store.getters.getUser.name;
          this.createClarificationRequest.username = this.$store.getters.getUser.username;
          this.createClarificationRequest.questionAnswerDto = this.answer.questionAnswerDto;
          const result = await RemoteServices.createClarificationRequest(this.answer.questionAnswerId, this.createClarificationRequest);
          this.$emit('new-clarification-request', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
  }

</script>