<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="tournaments"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-subheader class="font-weight-bold" style="font-size: 20px; color:black;">Tournaments</v-subheader>
        <v-row align="left">
          <v-col class="d-flex" cols="12" sm="4">
            <v-text-field
                    v-model="search"
                    append-icon="search"
                    label="Search"
                    class="mx-2"
            />
          </v-col>
          <v-col class="mt-3 d-flex" cols="12" sm="4">
            <v-select
                    :items="tournamentsFilters"
                    label="Tournaments"
                    dense
                    outlined
                    class="ml-5"
                    style="max-width: 300px;"
                    value="All Tournaments"
                    v-on:change="changeTournamentsFilter"
            />
          </v-col>
          <v-col class="mt-3" cols="12" sm="4" align="right">
            <v-btn
                    color="primary"
                    dark
                    @click="newTournament"
                    data-cy="createButton"
            >
              New Tournament
            </v-btn>
          </v-col>
        </v-row>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              x-medium
              class="mr-2"
              color="red"
              dark
              v-on="on"
              @click="cancelTournament(item)"
              data-cy="cancelTournament"
              >
              mdi-cancel
            </v-icon>
          </template>
          <span>Cancel Tournament</span>
        </v-tooltip>

        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
                    x-large
                    class="mr-2"
                    color="primary"
                    dark
                    v-on="on"
                    @click="enrollTournament(item)"
                    data-cy="enrollTournament"
            >
              mdi-location-enter
            </v-icon>
          </template>
          <span>Enroll Tournament</span>
        </v-tooltip>
      </template>

      <template v-slot:item.status="{ item }">
        <v-chip :color="getStatusColor(item.status)" small>
          <span>{{ item.status }}</span>
        </v-chip>
      </template>
    </v-data-table>

    <create-tournament-dialog
      v-if="currentTournament"
      v-model="createTournamentDialog"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
      :tournament="currentTournament"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';
import CreateTournamentDialog from '@/views/student/tournaments/CreateTournamentDialog.vue';

@Component({
  components: {
    'create-tournament-dialog': CreateTournamentDialog
  }
})
export default class CreateTournamentsView extends Vue {
  tournaments: Tournament[] = [];
  myTournaments: Tournament[] = [];
  allTournaments: boolean = true;
  currentTournament: Tournament | null = null;
  createTournamentDialog: boolean = false;
  search: string = '';
  tournamentsFilters: string[] = ["All Tournaments", "My Tournaments"];
  headers: object = [
    {
      text: 'Creator',
      value: 'creatorName',
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
      text: 'Tournament Topics',
      value: 'topicsName',
      align: 'center',
      width: '20%'
    },
    {
      text: 'Start Date',
      value: 'startTimeString',
      align: 'center',
      width: '10%'
    },
    {
      text: 'End Date',
      value: 'endTimeString',
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
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getTournaments();
      this.myTournaments = this.tournaments.filter(tournament => tournament.creatorName === this.$store.getters.getUser.name)
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async newTournament() {
    this.currentTournament = new Tournament();
    this.createTournamentDialog = true;
  }

  async onCreateTournament(tournament: Tournament) {
    this.tournaments.unshift(tournament);
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  async onCloseDialog() {
    this.createTournamentDialog = false;
    this.currentTournament = null;
  }

  async cancelTournament(tournamentToCancel: Tournament) {
    if (
      confirm('Are you sure you want to cancel?\nThis action in non-reversible')
    ) {
      try {
        await RemoteServices.cancelTournament(tournamentToCancel);
        this.tournaments = await RemoteServices.getCreatedTournaments();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
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

  getStatusColor(status: string) {
    if (status === 'OPEN') return 'green';
    else if (status === 'ONGOING') return 'yellow';
    else return 'red'
  }

  changeTournamentsFilter(value: string) {
    this.allTournaments = value === "All Tournaments";
  }
}
</script>

<style lang="scss" scoped>
.pos-text {
  text-align: left !important;
}
</style>
