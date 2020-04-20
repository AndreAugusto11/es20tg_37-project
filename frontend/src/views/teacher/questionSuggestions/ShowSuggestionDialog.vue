<template>
  <v-dialog
    :value="dialog"
    v-on:click:outside="closeSuggestionDialog"
    @keydown.esc="closeSuggestionDialog"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ questionSuggestion.questionDto.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-questionSuggestion :questionSuggestion="questionSuggestion" />
      </v-card-text>

      <v-card-actions v-if="!rejected">
        <v-btn
          dark
          color="green darken-1"
          @click="acceptSuggestionDialog"
          data-cy="acceptQuestion"
          >Accept</v-btn
        >
        <v-btn
          dark
          color="red darken-1"
          @click="rejectSuggestionDialog"
          data-cy="rejectQuestion"
          >Reject</v-btn
        >
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeSuggestionDialog"
          >close</v-btn
        >
      </v-card-actions>

      <v-card-text v-if="rejected">
        <v-textarea
          outline
          rows="2"
          label="Justification"
          v-model="justification.content"
          data-cy="justification_text"
        ></v-textarea>
      </v-card-text>

      <v-card-actions v-if="rejected">
        <v-spacer />
        <v-btn
          dark
          color="blue darken-1"
          @click="saveJustification"
          data-cy="saveJustification"
          >save</v-btn
        >
        <v-btn dark color="blue darken-1" @click="closeJustificationDialog"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>

    <v-card class="mt-5" v-if="questionSuggestion.justificationDto">
      <v-card-title>
        <span class="headline">Justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        <span
          v-html="
            convertMarkDown(
              questionSuggestion.justificationDto.content,
              questionSuggestion.justificationDto.image
            )
          "
        />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '../../../models/management/QuestionSuggestion';
import ShowQuestionSuggestion from '@/views/student/questionSuggestion/ShowQuestionSuggestion.vue';
import Justification from '@/models/management/Justification';
import Image from '@/models/management/Image';

@Component({
  components: {
    'show-questionSuggestion': ShowQuestionSuggestion
  }
})
export default class ShowSuggestionDialog extends Vue {
  @Prop({ type: QuestionSuggestion, required: true })
  readonly questionSuggestion!: QuestionSuggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
  @Prop({ type: Boolean, required: true }) rejected!: boolean;

  justification!: Justification;

  async created() {
    this.justification = new Justification();
  }

  closeSuggestionDialog() {
    if (this.rejected) this.rejected = false;
    this.justification.content = '';
    this.$emit('close-dialog');
  }

  acceptSuggestionDialog() {
    this.$emit('accept-suggestion');
  }

  rejectSuggestionDialog() {
    this.rejected = true;
  }

  closeJustificationDialog() {
    this.rejected = false;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async saveJustification() {
    if (this.justification && !this.justification.content) {
      await this.$store.dispatch('error', 'Justification must have content');
      return;
    } else {
      try {
        this.$emit('reject-suggestion', this.justification);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
