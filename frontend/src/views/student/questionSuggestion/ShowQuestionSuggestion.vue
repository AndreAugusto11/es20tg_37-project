<template>
  <div>
      <v-card-title class="mx-auto mb-5">
        <span class="headline">{{ questionSuggestion.questionDto.title }}</span>
      </v-card-title>

      <v-card-subtitle class="text-left mb-0">
        <span v-html="convertMarkDown(questionSuggestion.questionDto.content, null)" />
      </v-card-subtitle>
      
      <v-card-text v-if="this.questionSuggestion.questionDto.image">
        <span v-html="convertMarkDown('![image][image]', questionSuggestion.questionDto.image)"/>
      </v-card-text>

       <v-card-text class="text-left">
        <v-list class="text-left">
          <v-list-item v-for="option in questionSuggestion.questionDto.options.slice(0, 4)" :key="option.sequence" >
            <v-row>
              <v-icon v-if="option.correct" class="dark">mdi-checkbox-marked-circle</v-icon>
              <v-icon v-else>mdi-checkbox-blank-circle-outline</v-icon>
              <v-col>
                <v-card-text v-html="convertMarkDown(option.content)" />
              </v-col>
            </v-row>
          </v-list-item>
        </v-list>
      </v-card-text>
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

<style lang="scss">
    img {
        margin-left: auto;
        margin-right: auto;
    }

    .v-application p {
      margin-bottom: 0;
    }
</style>