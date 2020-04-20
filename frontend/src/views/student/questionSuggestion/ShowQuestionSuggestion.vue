<template>
  <div>
    <span
      v-html="
        convertMarkDown(
          questionSuggestion.questionDto.content,
          questionSuggestion.questionDto.image
        )
      "
    />
    <ul>
      <li
        v-for="option in questionSuggestion.questionDto.options"
        :key="option.number"
      >
        <span
          v-if="option.correct"
          v-html="convertMarkDown('**[★]** ', null)"
        />
        <span
          v-html="convertMarkDown(option.content, null)"
          v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import Image from '@/models/management/Image';

@Component
export default class ShowQuestionSuggestion extends Vue {
  @Prop({ type: QuestionSuggestion, required: true })
  readonly questionSuggestion!: QuestionSuggestion;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
