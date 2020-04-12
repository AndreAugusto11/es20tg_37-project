<template>
    <div class="container">
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
                <v-col>
                    <p class="post-text">{{ this.clarificationRequest.content }}</p>
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
                <v-col>
                    <p class="post-text">{{ this.clarificationRequest.questionAnswerDto.question.content }}</p>
                </v-col>
            </v-row>
            <v-list>
                <v-list-item class="post-text" v-for="item in this.clarificationRequest.questionAnswerDto.question.options" :key="item.content">
                    <v-list-item-icon>
                        <v-icon v-if="item.correct" color="green">mdi-check</v-icon>
                        <v-icon v-else-if="chosen(item.content)" color="yellow">mdi-help</v-icon>
                        <v-icon v-else color="red">mdi-close</v-icon>
                    </v-list-item-icon>

                    <v-list-item-content>
                        <v-list-item-title v-text="item.content"></v-list-item-title>
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-card>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
  import Question from '@/models/management/Question';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';

  @Component
  export default class ClarificationRequestView extends Vue {
    clarificationRequest!: ClarificationRequest;
    statusList = ['OPEN', 'CLOSED'];

    headers: object = [
      { text: 'Clarification', value: 'content', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' }
    ];

    created() {
        this.clarificationRequest = JSON.parse(this.$route.params.clarificationRequest);
    }

    customFilter(value: string, search: string, question: Question) {
      // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
      return (
        search != null &&
        JSON.stringify(question)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
      );
    }

    convertMarkDownNoFigure(text: string, image: Image | null = null): string {
      return convertMarkDownNoFigure(text, image);
    }

    getStatusColor(status: string) {
      if (status === 'CLOSE') return 'red';
      else return 'green';
    }

    chosen(content: string) {
      return content === this.clarificationRequest.questionAnswerDto.option.content;
    }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left;
    }
</style>
