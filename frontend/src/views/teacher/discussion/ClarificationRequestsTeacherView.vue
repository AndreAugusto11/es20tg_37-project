<template>
  <v-card class="table">
    <v-skeleton-loader
            v-if="this.$store.getters.getLoading"
            ref="skeleton"
            type="table"
            class="mx-auto"
    ></v-skeleton-loader>
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
            @click:row="openClarificationRequest"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
                  v-model="search"
                  append-icon="search"
                  label="Search"
                  class="mx-2"
                  data-cy="Search"
          />
        </v-card-title>
      </template>

      <template v-slot:item.content="{ item }">
        <p v-html="convertMarkDown(item.content, null)" />
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>

      <template v-slot:item.public="{ item }">
         <v-tooltip v-if="item.public" left>
            <template v-slot:activator="{ on }">
              <v-icon class="mr-2" color="green" v-on="on">fas fa-lock-open</v-icon>
            </template>
            <span>Public</span>
          </v-tooltip>

          <v-tooltip v-else left>
            <template v-slot:activator="{ on }">
              <v-icon class="mr-2" color="red" v-on="on">fas fa-lock</v-icon>
            </template>
            <span>Private</span>
          </v-tooltip>
      </template>
      
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
    import { Component, Vue } from 'vue-property-decorator';
    import RemoteServices from '@/services/RemoteServices';
    import { convertMarkDown } from '@/services/ConvertMarkdownService';
    import Question from '@/models/management/Question';
    import Image from '@/models/management/Image';
    import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';

    @Component
    export default class ClarificationRequestsTeacherView extends Vue {
        clarificationRequests: ClarificationRequest[] = [];
        search: string = '';

        headers: object = [
            { text: 'Clarification', value: 'content', align: 'left', width: '50%' },
            { text: 'Student Name', value: 'name', align: 'center', width: '20%' },
            { text: 'Status', value: 'status', align: 'center', width: '20%' },
            { text: 'Availability', value: 'public', align: 'center', width: '10%'}
        ];

        async created() {
            await this.$store.dispatch('loading');
            try {
                this.clarificationRequests = await RemoteServices.getClarificationRequests();
            } catch (error) {
                await this.$store.dispatch('error', error);
            }
            await this.$store.dispatch('clearLoading');
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

      convertMarkDown(text: string, image: Image | null = null): string {
            return convertMarkDown(text, image);
        }

        getStatusColor(status: string) {
            if (status === 'CLOSED') return 'red';
            else return 'green';
        }

        async openClarificationRequest(value: ClarificationRequest) {
            await this.$router.push({ name: 'discussionQuestion', params: { clarificationRequest: JSON.stringify(value) } });
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
