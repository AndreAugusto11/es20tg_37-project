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

      <v-card-text class="text-left" v-if="currentTournament">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="currentTournament.numberQuestions"
                label="Number Of Questions"
                data-cy="numQuest"
              />
            </v-flex>
            <v-select
                v-model="topicsSelected"
                :items="topicsAll"
                item-text="name"
                attach
                chips
                label="Topics"
                multiple
                deletable-chips
                hide-selected
                data-cy="topicSelect"
            />
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="startString"
                label="Start Time (format = YYYY-MM-DD HH:MM)"
                data-cy="start"
              />
            </v-flex>
            <v-flex xs24 sm12 md8>
              <v-text-field
                v-model="endString"
                label="End Time (format = YYYY-MM-DD HH:MM)"
                data-cy="end"
              />
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('close-dialog', false)">
          Cancel
        </v-btn>
        <v-btn dark color="blue darken-1" @click="saveTournament" data-cy="save">
          Save
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';

@Component
export default class CreateTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  currentTournament!: Tournament;
  topicsAll: Topic[] = [];
  topicsSelected: string[] = [];
  stringAux: String[] | null = null;
  startString: string = '';
  endString: string = '';

  async created() {
    try {
      this.currentTournament = new Tournament(this.tournament);
      this.topicsAll = await Promise.all(await RemoteServices.getTopics());
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async saveTournament() {
    this.currentTournament.topics = this.topicsAll.filter(topic => this.topicsSelected.includes(topic.name));
    console.log(this.currentTournament.topics);
    if (
      this.currentTournament &&
      (!this.currentTournament.numberQuestions ||
        !this.currentTournament.topics.length ||
        !this.currentTournament.startTimeString ||
        !this.currentTournament.endTimeString)
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have a Number of Questions, topic(s), a start and end time'
      );
      return;
    }
    this.currentTournament.startTimeString = this.startString;
    this.currentTournament.endTimeString = this.endString;
    this.stringAux = this.currentTournament.startTimeString.split(/ |:|-/);
    this.currentTournament.startTime = this.stringAux.map(Number);
    this.stringAux = this.currentTournament.endTimeString.split(/ |:|-/);
    this.currentTournament.endTime = this.stringAux.map(Number);
    try {
      const result = await RemoteServices.createTournament(this.currentTournament);
      this.$emit('new-tournament', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
