<template>
  <div>
    <span v-html="convertMarkDown(questionSuggestion.questionDto.content, questionSuggestion.questionDto.image)" />
    <br />
    <ul>
      <li v-for="option in questionSuggestion.questionDto.options.slice(0, 4)" :key="option.sequence" >
        <span
            v-if="option.correct"
            v-html="convertMarkDown('**[★]** ' + option.content)"
            v-bind:class="[option.correct ? 'font-weight-bold' : '']"
        />
        <span v-else v-html="convertMarkDown(option.content)" />
      </li>
    </ul>
    <br />
  </div>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import QuestionSuggestion from '@/models/management/QuestionSuggestion';
  import Image from '@/models/management/Image';

  @Component
  export default class ShowQuestionSuggestion extends Vue {
    @Prop({ type: QuestionSuggestion, required: true }) readonly questionSuggestion!: QuestionSuggestion;

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }
  }
</script>
