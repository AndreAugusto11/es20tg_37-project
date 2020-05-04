<template>
    <v-card
            class="mx-auto mt-10"
            max-width="1000"
            outlined
    >
        <v-row
                align="center"
                class="spacer ml-5 mt-5"
                no-gutters
        >
            <v-col
                    sm="2"
                    md="9"
            >
                <h2 class="mb-1 post-text">{{ this.clarificationRequest.name }}</h2>
            </v-col>
            <v-col>
                <v-chip :color="getStatusColor(this.clarificationRequest.status)" small>
                    <span>{{ this.clarificationRequest.status }}</span>
                </v-chip>
            </v-col>
        </v-row>
        <v-row
                align="center"
                class="spacer ml-5 mb-5"
                no-gutters
        >
            <v-col class="post-text">
                <span v-html="convertMarkDown(this.clarificationRequest.content, null)" />
            </v-col>
        </v-row>
        <v-divider></v-divider>
        <v-row
                align="center"
                class="spacer"
                no-gutters
        >
            <v-col>
                <v-subheader>Question</v-subheader>
            </v-col>
        </v-row>
        <v-row
                align="center"
                class="spacer ml-5"
                no-gutters
        >
            <v-col class="post-text">
                <span class="post-text" v-html="convertMarkDown(this.clarificationRequest.questionAnswerDto.question.content, null)" />
            </v-col>
        </v-row>
        <v-list>
            <v-list-item class="post-text" v-for="item in this.clarificationRequest.questionAnswerDto.question.options" :key="item.content">
                <v-list-item-icon>
                    <v-tooltip v-if="item.correct" left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="green" v-on="on">mdi-check</v-icon>
                        </template>
                        <span>Correct option</span>
                    </v-tooltip>
                    <v-tooltip v-else-if="chosen(item.content)" left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="yellow darken-3" v-on="on">mdi-help</v-icon>
                        </template>
                        <span>Student's option</span>
                    </v-tooltip>
                    <v-tooltip v-else left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="red" v-on="on">mdi-close</v-icon>
                        </template>
                        <span>Incorrect option</span>
                    </v-tooltip>
                </v-list-item-icon>

                <v-list-item-content>
                    <span v-html="convertMarkDown(item.content, null)" />
                </v-list-item-content>
            </v-list-item>
        </v-list>
        <!-- <v-list>
            <v-list-item class="post-text" v-for="item in this.clarificationRequest.questionAnswerDto.question.options" :key="item.content">
                <v-tooltip left>
                    <template v-slot:activator="{ on }">
                        <v-list-item-icon>
                            <v-icon v-if="item.correct" color="green" v-on="on">mdi-check</v-icon>
                            <v-icon v-else-if="chosen(item.content)" color="yellow" v-on="on">mdi-help</v-icon>
                            <v-icon v-else color="red" v-on="on">mdi-close</v-icon>
                        </v-list-item-icon>

                        <v-list-item-content>
                            <span v-on="on" v-html="convertMarkDownNoFigure(item.content, null)" />
                        </v-list-item-content>
                    </template>
                    <span v-if="item.correct">Correct option</span>
                    <span v-else-if="chosen(item.content)">Student's option</span>
                    <span v-else >Incorrect option</span>
                </v-tooltip>
            </v-list-item>
        </v-list> -->
    </v-card>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';

  @Component
  export default class ClarificationRequestComponent extends Vue {
    @Prop(ClarificationRequest) readonly clarificationRequest!: ClarificationRequest;

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }

    getStatusColor(status: string) {
      if (status === 'CLOSED') return 'red';
      else if (status === 'ANSWERED') return 'yellow';
      else return 'green'
    }

    chosen(content: string) {
      return content === this.clarificationRequest.questionAnswerDto.option.content;
    }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
