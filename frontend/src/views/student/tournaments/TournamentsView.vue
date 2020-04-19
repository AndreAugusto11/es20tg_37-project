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
                </v-card-title>
            </template>
            <template v-slot:item.action="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                x-large
                                class="mr-2"
                                color="primary" dark
                                v-on="on"
                                @click="enrollTournament(item)"
                                data-cy="enrollTournament"
                        >mdi-location-enter</v-icon
                        >
                    </template>
                    <span>Enroll Tournament</span>
                </v-tooltip>
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
      text: 'Tournament Topics',
      value: 'topicsName',
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

  async enrollTournament(tournamentToEnroll: Tournament) {
    if (confirm('Are you sure you want to enroll?')) {
      try {
        await RemoteServices.enrollTournament(tournamentToEnroll);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
    .pos-text {
        text-align: left !important;
    }
</style>