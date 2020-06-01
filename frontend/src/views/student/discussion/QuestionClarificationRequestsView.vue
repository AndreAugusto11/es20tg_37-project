<template>
  <div>
    <v-card class="table">
      <v-row
              align="center"
              class="spacer"
              no-gutters
      >
          <v-col>
              <v-subheader style="font-size: 20px; color:black;" class="mb-2 font-weight-bold">Question</v-subheader>
          </v-col>
      </v-row>
      <v-row
              align="center"
              class="spacer ml-5"
              no-gutters
      >
          <v-col class="post-text">
              <span class="post-text" style="text-align: left;" v-html="convertMarkDown(this.question.content, null)" />
          </v-col>
      </v-row>
      <v-list align="left">
          <v-list-item class="post-text" v-for="item in this.question.options" :key="item.content">
              <v-list-item-icon>
                <v-icon small >mdi-checkbox-blank-circle</v-icon>
              </v-list-item-icon>

              <v-list-item-content class="pt-5">
                <span v-html="convertMarkDown(item.content, null)" />
              </v-list-item-content>
          </v-list-item>
      </v-list>
    </v-card>
      
    <v-card class="table">
      <v-skeleton-loader
              v-if="this.$store.getters.getLoading"
              ref="skeleton"
              type="table"
              class="mx-auto"
      ></v-skeleton-loader>
      
      <v-divider></v-divider>
      <v-data-table
              v-if="!this.$store.getters.getLoading"
              :headers="headers"
              :custom-filter="customFilter"
              :items="clarificationRequests"
              :search="search"
              multi-sort
              :mobile-breakpoint="0"
              :items-per-page="15"
              :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
              @click:row='openClarificationRequest'
      >
          <template v-slot:top>
              <v-subheader class="font-weight-bold" style="font-size: 20px; color:black;">Clarification Requests</v-subheader>
              <v-card-title>
                  <v-text-field
                          v-model="search"
                          append-icon="search"
                          label="Search"
                          data-cy="Search"
                          class="mx-2"
                          style="margin-top: -20px;"
                  />
              </v-card-title>
          </template>

          <template v-slot:item.content="{ item }">
              <p v-html="convertMarkDown(item.content, null)" />
          </template>

          <template v-slot:item.number="{ item }">
              <p v-html="convertMarkDown(item.numberOfAnswers.toString(), null)" />
          </template>

          <template v-slot:item.status="{ item }">
              <v-chip :color="getStatusColor(item.status)" small>
                  <span>{{ item.status }}</span>
              </v-chip>
          </template>

          <template v-slot:item.public="{ item }">
            <v-tooltip v-if="item.public" left>
                <template v-slot:activator="{ on }">
                  <v-icon class="mr-2" color="green" v-on="on">mdi-eye</v-icon>
                </template>
                <span>Public</span>
            </v-tooltip>

            <v-tooltip v-else left>
              <template v-slot:activator="{ on }">
                <v-icon class="mr-2" color="red" v-on="on">mdi-eye-off</v-icon>
              </template>
              <span>Private</span>
            </v-tooltip>
          </template>

      </v-data-table>
    </v-card>
  </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import Question from '@/models/management/Question';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';

  @Component({
    components: {
    }
  })

  export default class QuestionClarificationRequestsView extends Vue {
    question!: Question;
    clarificationRequests: ClarificationRequest[] = [];
    search: string = '';

    headers: object = [
      { text: 'Clarification', value: 'content', align: 'left', width: '30%' },
      { text: 'Student Name', value: 'username', align: 'left', width: '10%' },
      { text: 'Creation Date', value: 'creationDate', align: 'center', width: '10%' },
      { text: 'Replies', value: 'number', align: 'center', width: '10%' },
      { text: 'Status', value: 'status', align: 'center', width: '5%' },
      { text: 'Availability', value: 'public', align: 'center', width: '10%'}
    ];

    async created() {
      this.question = new Question(JSON.parse(this.$route.params.question));

      await this.$store.dispatch('loading');
      try {
        if (this.question.id != null)
          this.clarificationRequests = await RemoteServices.getQuestionClarificationRequests(this.question.id);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    customFilter(value: string, search: string, question1: Question) {
      // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
      return (
        search != null &&
        JSON.stringify(question1)
          .toLowerCase()
          .indexOf(search.toLowerCase()) !== -1
      );
    }

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }

    getStatusColor(status: string) {
      if (status === 'CLOSED') return 'red';
      else if (status === 'ANSWERED') return 'orange';
      else return 'green'
    }

    async openClarificationRequest(value: ClarificationRequest) {
      await this.$router.push({ name: 'clarification', params: { clarificationRequest: JSON.stringify(value) } });
    }
  }
</script>

<style lang="scss" scoped>
    .question-textarea {
        text-align: left;

        .CodeMirror,
        .CodeMirror-scroll {
            min-height: 200px !important;
        }
    }
    .option-textarea {
        text-align: left;

        .CodeMirror,
        .CodeMirror-scroll {
            min-height: 100px !important;
        }
    }
</style>
