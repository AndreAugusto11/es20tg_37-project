<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog', false)"
    @keydown.esc="$emit('close-dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card v-bind:class="[startDateOpen || endDateOpen ? 'activeDate' : 'notActiveDate']">
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
            <v-flex xs24 sm12 md8 style="margin-left: -10px;">
              <VueCtkDateTimePicker
                      label="*Start Date"
                      id="availableDateInput"
                      v-model="currentTournament.startTime"
                      format="YYYY-MM-DDTHH:mm:ssZ"
                      v-on:is-shown="changeStartDateOpen(true)"
                      v-on:is-hidden="changeStartDateOpen(false)"
              ></VueCtkDateTimePicker>
            </v-flex>
            <v-flex xs24 sm12 md8 style="margin-left: -10px;">
                <VueCtkDateTimePicker
                      label="*End Date"
                      id="endDateInput"
                      v-model="currentTournament.endTime"
                      format="YYYY-MM-DDTHH:mm:ssZ"
                      v-on:is-shown="changeEndDateOpen(true)"
                      v-on:is-hidden="changeEndDateOpen(false)"
              ></VueCtkDateTimePicker>
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
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component
export default class CreateTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  currentTournament!: Tournament;
  topicsAll: Topic[] = [];
  topicsSelected: string[] = [];
  startDateOpen: boolean = false;
  endDateOpen: boolean = false;

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
    console.log(this.currentTournament.startTime);
    if (
      this.currentTournament &&
      (!this.currentTournament.numberQuestions ||
        !this.currentTournament.topics.length ||
        !this.currentTournament.startTime ||
        !this.currentTournament.endTime)
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have a Number of Questions, topic(s), a start and end date'
      );
      return;
    }

    try {
      const result = await RemoteServices.createTournament(this.currentTournament);
      this.$emit('new-tournament', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  changeStartDateOpen(boolean: boolean) {
    this.startDateOpen = boolean;
  }

  changeEndDateOpen(boolean: boolean) {
    this.endDateOpen = boolean;
  }
}
</script>

<style lang="scss" scoped>
  .activeDate {
    height: 800px !important;
  }

  .notActiveDate {
  }
</style>
