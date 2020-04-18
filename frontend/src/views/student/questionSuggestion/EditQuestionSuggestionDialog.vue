<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          New Suggestion
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editQuestionSuggestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editQuestionSuggestion.questionDto.title"
                label="Title"
                data-cy="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editQuestionSuggestion.questionDto.content"
                label="Content"
                data-cy="Content"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in editQuestionSuggestion.questionDto.options.length"
              :key="index"
            >
              <v-switch
                v-model="
                  editQuestionSuggestion.questionDto.options[index - 1].correct
                "
                class="ma-4"
                label="Correct"
                data-cy="Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="
                  editQuestionSuggestion.questionDto.options[index - 1].content
                "
                label="Content"
                data-cy="Option"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)" data-cy="cancelButton"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveQuestionSuggestion"  data-cy="saveButton"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class EditQuestionSuggestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionSuggestion, required: true })
  readonly questionSuggestion!: QuestionSuggestion;

  editQuestionSuggestion!: QuestionSuggestion;

  created() {
    this.editQuestionSuggestion = new QuestionSuggestion(
      this.questionSuggestion
    );
  }

  async saveQuestionSuggestion() {
    if (
      this.editQuestionSuggestion &&
      (!this.editQuestionSuggestion.questionDto.title ||
        !this.editQuestionSuggestion.questionDto.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Suggestion must have title and content'
      );
      return;
    } else {
      try {
        const result = await RemoteServices.createQuestionSuggestion(
          this.editQuestionSuggestion
        );
        console.log(result);
        this.$emit('save-questionSuggestion', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>