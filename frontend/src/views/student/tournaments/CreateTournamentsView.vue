<template>
  <v-card class="table">
    <div>
      <h1>Created Tournaments</h1>
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
          <v-btn
            color="primary"
            dark
            @click="newTournament"
            data-cy="createButton"
            >Create New Tournament</v-btn
          >
        </v-card-title>
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
              >mdi-cancel</v-icon
            >
          </template>
          <span>Cancel Tournament</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="editTournamentDialog"
      v-on:new-tournament="onCreateTournament"
      v-on:close-dialog="onCloseDialog"
      :tournament="currentTournament"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { Tournament } from '@/models/tournaments/Tournament';
import RemoteServices from '@/services/RemoteServices';
import EditTournamentDialog from '@/views/student/tournaments/EditTournamentDialog.vue';

@Component({
  components: {
    'edit-tournament-dialog': EditTournamentDialog
  }
})
export default class CreateTournamentsView extends Vue {
  tournaments: Tournament[] = [];
  currentTournament: Tournament | null = null;
  editTournamentDialog: boolean = false;
  tournamentDialog: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Tournament ID',
      value: 'id',
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
      this.tournaments = await RemoteServices.getCreatedTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async newTournament() {
    this.currentTournament = new Tournament();
    this.editTournamentDialog = true;
  }

  async onCreateTournament(tournament: Tournament) {
    this.tournaments.unshift(tournament);
    this.editTournamentDialog = false;
    this.currentTournament = null;
  }

  async onCloseDialog() {
    this.editTournamentDialog = false;
    this.currentTournament = null;
  }

  showTournamentDialog(tournament: Tournament) {
    this.currentTournament = tournament;
    this.tournamentDialog = true;
  }

  async cancelTournament(tournamentToCancel: Tournament) {
    if (
      confirm('Are you sure you want to cancel?\nThis action in non-reversible')
    ) {
      try {
        /*await RemoteServices.cancelTournament(tournamentToCancel);*/
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
