<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="closeQuestionSuggestionDialog"
    max-width="65%"
    max-height="80%"
  >
    <v-card>
      <v-card-text class="text-left">
        <show-questionSuggestion :questionSuggestion="questionSuggestion" />
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          dark
          color="blue darken-1"
          @click="closeQuestionSuggestionDialog"
          data-cy="closeButton"
          >close</v-btn
        >
      </v-card-actions>
    </v-card>

    <v-card class="mt-5" v-if="questionSuggestion.justificationDto">
      <v-card-title>
        <span class="headline">Justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        <span v-html="convertMarkDown(questionSuggestion.justificationDto.content, null)" />
      </v-card-text>

      <v-card-text class="text-left" v-if="questionSuggestion.justificationDto.image">
        <span v-html="convertMarkDown('![image][image]', questionSuggestion.justificationDto.image)" />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import QuestionSuggestion from '../../../models/management/QuestionSuggestion';
import ShowQuestionSuggestion from '@/views/student/questionSuggestion/ShowQuestionSuggestion.vue';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';

@Component({
  components: {
    'show-questionSuggestion': ShowQuestionSuggestion
  }
})
export default class ShowQuestionSuggestionDialog extends Vue {
  @Prop({ type: QuestionSuggestion, required: true })
  readonly questionSuggestion!: QuestionSuggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  closeQuestionSuggestionDialog() {
    this.$emit('close-dialog');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped>
    .v-application p {
      margin-bottom: 0 !important;
    }
</style>