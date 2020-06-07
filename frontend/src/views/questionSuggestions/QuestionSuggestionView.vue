<template>
  <div>
    <v-card class="mx-auto mt-10 mb-10" max-width="1100" outlined>
      <!--<v-card-title class="mb-2">
        <span class="headline">{{ questionSuggestion.questionDto.title }}</span>
      </v-card-title>-->

      <v-row align="center" class="spacer ml-5 mr-5 mt-5" no-gutters>
        <v-col md="auto" class="mr-5">
          <v-avatar :color="this.getRandomVuetifyColor(this.questionSuggestion.name)">
            <span
              class="white--text headline"
            >{{ this.getNameInitials(this.questionSuggestion.name) }}</span>
          </v-avatar>
        </v-col>

        <v-col sm="3" md="7">
          <h2 class="mb-1 text-left">{{ this.questionSuggestion.name }}</h2>
        </v-col>

        <v-col align="right" class="mr-5">
          <v-chip :color="getStatusColor(this.questionSuggestion.status)" small>
            <span>{{ this.questionSuggestion.status }}</span>
          </v-chip>
        </v-col>
      </v-row>

      <v-card-text class="mt-3" v-if="this.questionSuggestion.questionDto.image">
        <span v-html="convertMarkDown('![image][image]', questionSuggestion.questionDto.image)" />
      </v-card-text>

      <v-divider v-if="this.questionSuggestion.questionDto.image"></v-divider>

      <v-row align="center" class="spacer" no-gutters>
        <v-col>
          <v-subheader>Question</v-subheader>
        </v-col>
      </v-row>
      <v-row align="center" class="spacer ml-5" no-gutters>
        <v-col class="text-left">
          <span
            class="text-left"
            v-html="convertMarkDown(questionSuggestion.questionDto.content, null)"
          />
        </v-col>
      </v-row>

      <v-card-text class="text-left">
        <v-list>
          <v-list-item
            class="row"
            v-for="option in questionSuggestion.questionDto.options"
            :key="option.sequence"
          >
            <v-list-item-icon>
              <v-tooltip v-if="option.correct" left>
                <template v-slot:activator="{ on }">
                  <v-icon color="green" v-on="on">mdi-check</v-icon>
                </template>
                <span>Correct option</span>
              </v-tooltip>

              <v-tooltip v-else left>
                <template v-slot:activator="{ on }">
                  <v-icon color="red" v-on="on">mdi-close</v-icon>
                </template>
                <span>Incorrect option</span>
              </v-tooltip>
            </v-list-item-icon>
            <v-list-item-content>
              <span v-html="convertMarkDown(option.content, null)" />
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>

    <v-card
      class="mx-auto mb-10"
      max-width="1100"
      outlined
      v-if="questionSuggestion.justificationDto"
    >
      <v-card-title>
        <span class="headline">Justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        <span v-html="convertMarkDown(questionSuggestion.justificationDto.content, null)" />
      </v-card-text>

      <v-card-text v-if="questionSuggestion.justificationDto.image">
        <span
          v-html="convertMarkDown('![image][image]', questionSuggestion.justificationDto.image)"
        />
      </v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import Image from '@/models/management/Image';
import { getNameInitials } from '@/services/GetNameInitialsService';
import { getRandomVuetifyColor } from '@/services/GetRandomVuetifyColorService';

@Component
export default class QuestionSuggestionView extends Vue {
  questionSuggestion!: QuestionSuggestion;

  async created() {
    this.questionSuggestion = new QuestionSuggestion(
      JSON.parse(this.$route.params.questionSuggestion)
    );
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'PENDING') return 'orange';
    else return 'green';
  }

  getNameInitials(name: string): string {
    return getNameInitials(name);
  }

  getRandomVuetifyColor(name: string): string {
    return getRandomVuetifyColor(name);
  }
}
</script>

<style lang="scss">
img {
  max-width: 90%;
  margin-left: auto;
  margin-right: auto;
}

.v-application p {
  margin-bottom: 0 !important;
}
</style>