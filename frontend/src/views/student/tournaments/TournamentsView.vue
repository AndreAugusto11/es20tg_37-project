<template>
    <v-card class="table">
    <div>
        <h1>Tournaments View</h1>
    </div>
        <v-data-table
                :headers="headers"
                :items="tournaments"
                :search="search"
                disable-pagination
                :hide-default-footer="true"
                :mobile-breakpoint="0"
                multi-sort
        >
            <template v-slot:top>
                <v-card-title>
                    <v-text-field
                            v-model="search"
                            append-icon="search"
                            label="Search"
                            class="mx-2"
                    />
                    <v-spacer />
                    <v-btn color="primary" dark @click="newTournament" data-cy="createButton"
                    >New Tournament</v-btn
                    >
                </v-card-title>
            </template>
        </v-data-table>
    </v-card>
</template>

<script lang="ts">
import { Component, Vue }  from 'vue-property-decorator';
import { Tournament } from '@/models/tournaments/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class TournamentsView extends Vue {
  tournaments: Tournament[] = [];
  search: string = '';
  headers: object = [
    {
      text: 'Tournament ID',
      value: 'id',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Start Date',
      value: 'startTime',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Closure Date',
      value: 'endTime',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Number of Questions',
      value: 'numQuests',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Status',
      value: 'status',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false,
      width: '20%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
    .pos-text {
        text-align: left !important;
    }
</style>