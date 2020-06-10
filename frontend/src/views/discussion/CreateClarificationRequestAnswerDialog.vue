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
        <span class="headline">New Reply</span>
      </v-card-title>

            <v-card-text class="text-left" v-if="createClarificationRequestAnswer">
                <v-container grid-list-md fluid>
                    <v-layout column wrap>
                        <v-flex xs24 sm12 md8>
                            <v-textarea
                                    outline
                                    rows="5"
                                    v-model="createClarificationRequestAnswer.content"
                                    label="Content"
                                    data-cy="Content"
                            ></v-textarea>
                        </v-flex>
                    </v-layout>
                </v-container>
                <template>
                  <v-file-input
                  class="pr-3"
                  label="File input"
                  show-size
                  outlined
                  counter
                  dense
                  @change="constructImage($event)"
                  accept="image/*"
                  />
                </template>
            </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('close-dialog')" data-cy="cancelButton">Cancel</v-btn>
        <v-btn
          color="blue darken-1"
          @click="saveClarificationRequestAnswer"
          data-cy="saveButton"
        >Send</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Model, Prop, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import { ClarificationRequestAnswer } from '@/models/discussion/ClarificationRequestAnswer';

@Component
export default class CreateClarificationRequestAnswerDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationRequest, required: true })
  readonly clarificationRequest!: ClarificationRequest;

    createClarificationRequestAnswer!: ClarificationRequestAnswer;
    file: File | null = null;

  created() {
    this.createClarificationRequestAnswer = new ClarificationRequestAnswer(
      this.createClarificationRequestAnswer
    );
  }

    async constructImage(event: File) {
        this.file = event;
    }

    async saveClarificationRequestAnswer() {
      if (
        this.createClarificationRequestAnswer &&
        (!this.createClarificationRequestAnswer.content)
      ) {
        await this.$store.dispatch(
          'error',
          'Clarification request answer must have content'
        );
        return;
      }

      if (this.createClarificationRequestAnswer && this.clarificationRequest.id) {
        try {
          this.createClarificationRequestAnswer.name = this.$store.getters.getUser.name;
          this.createClarificationRequestAnswer.username = this.$store.getters.getUser.username;
          this.createClarificationRequestAnswer.type = this.$store.getters.isTeacher ? 'TEACHER_ANSWER' : 'STUDENT_ANSWER';
          const result = await RemoteServices.createClarificationRequestAnswer(this.clarificationRequest.id, this.createClarificationRequestAnswer);
          if (result.id != null && this.file) {
            result.image = new Image();
            result.image.url = await RemoteServices.uploadClarificationRequestAnswerImage(result.id, this.file);
          }
          this.$emit('new-clarification-request-answer', result);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }

  changeAvailability() {
    this.clarificationRequest.public = this.clarificationRequest.public
      ? false
      : true;
  }
}
</script>
