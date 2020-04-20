<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog', false)"
    @keydown.esc="$emit('close-dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          New Tournament
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editTournament">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="editTournament.numQuests"
                label="Number Of Questions"
                data-cy="numQuest"
              />
            </v-flex>
            <template>
              <edit-tournament-topics
                :key="compoundKey"
                :tournament="editTournament"
                :topics="this.topicsAll"
                v-on:tournament-changed-topics="retrieveTopics"
                data-cy="topicSearch"
              />
            </template>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="startString"
                label="Start Time (format = YYYY-MM-DD HH:MM)"
                data-cy="start"
              ></v-text-field>
            </v-flex>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="endString"
                label="End Time (format = YYYY-MM-DD HH:MM)"
                data-cy="end"
              ></v-text-field>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('close-dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveTournament" data-cy="save"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';
import EditTournamentTopics from '@/views/student/tournaments/EditTournamentTopics.vue';

@Component({
  components: {
    'edit-tournament-topics': EditTournamentTopics
  }
})
export default class EditTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  editTournament!: Tournament;
  topicsAll: Topic[] = [];
  stringAux: String[] | null = null;
  startString: string = '';
  endString: string = '';
  compoundKey: number = 0;

  async created() {
    try {
      this.editTournament = new Tournament(this.tournament);
      this.editTournament.topics = [];
      this.editTournament.topicsName = [];
      this.topicsAll = await Promise.all(await RemoteServices.getTopics());
      console.log(this.topicsAll);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
    this.forceRenderer();
  }

  forceRenderer() {
    this.compoundKey += 1;
  }

  async retrieveTopics(topics: Topic[]) {
    for (var t in topics) {
      if (this.editTournament.topics && this.editTournament.topicsName) {
        this.editTournament.topics.unshift(topics[t].id);
        this.editTournament.topicsName.unshift(topics[t].name);
      } else {
        this.editTournament.topics = [topics[t].id];
        this.editTournament.topicsName = [topics[t].name];
      }
    }
  }

  async saveTournament() {
    if (
      this.editTournament &&
      (!this.editTournament.numQuests ||
        !this.editTournament.topics ||
        !this.editTournament.startTimeString ||
        !this.editTournament.endTimeString)
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have a Number of Questions, topic(s), a start and end time'
      );
      return;
    }
    this.editTournament.startTimeString = this.startString;
    this.editTournament.endTimeString = this.endString;
    this.stringAux = this.editTournament.startTimeString.split(/ |:|-/);
    this.editTournament.startTime = this.stringAux.map(Number);
    this.stringAux = this.editTournament.endTimeString.split(/ |:|-/);
    this.editTournament.endTime = this.stringAux.map(Number);
    try {
      const result = await RemoteServices.createTournament(this.editTournament);
      this.$emit('new-tournament', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
