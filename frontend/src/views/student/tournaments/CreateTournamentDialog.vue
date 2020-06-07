<template>
  <v-card class="table">
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
              v-model="currentTournament.title"
              label="Title"
              data-cy="title"
            />
          </v-flex>
          <v-flex xs24 sm12 md8>
            <v-text-field
              v-model="currentTournament.numberOfQuestions"
              label="Number Of Questions"
              data-cy="numQuest"
            />
          </v-flex>
          <v-select
            v-model="assessmentsSelected"
            :items="availableAssessments"
            item-text="title"
            attach
            chips
            label="Topics from Assessments"
            multiple
            deletable-chips
            hide-selected
            data-cy="topicSelect"
          />
          <v-flex xs24 sm12 md8 style="margin-left: -10px;">
            <VueCtkDateTimePicker
              label="*Available Date"
              id="startDateInput"
              v-model="currentTournament.availableDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
            />
          </v-flex>
          <v-flex xs24 sm12 md8 style="margin-left: -10px;">
            <VueCtkDateTimePicker
              label="*Conclusion Date"
              id="endDateInput"
              v-model="currentTournament.conclusionDate"
              format="YYYY-MM-DDTHH:mm:ssZ"
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
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import { Tournament } from '@/models/tournaments/Tournament';
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';
import Assessment from '@/models/management/Assessment';

Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);

@Component
export default class CreateTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  currentTournament: Tournament | null = null;
  topicsAll: Topic[] = [];
  availableAssessments: Assessment[] = [];
  assessmentsSelected: string[] = [];

  async created() {
    try {
      this.currentTournament = new Tournament(this.tournament);
      this.topicsAll = await Promise.all(await RemoteServices.getTopics());
      this.availableAssessments = await Promise.all(
        await RemoteServices.getAvailableAssessments()
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async saveTournament() {
    if (this.currentTournament) {
      let test = this.availableAssessments.filter(assessment =>
        this.assessmentsSelected.includes(assessment.title)
      );
      test.forEach(test =>
        test.topicConjunctions.forEach(topic =>
          this.currentTournament?.topicConjunctions.push(topic)
        )
      );
      console.log(this.currentTournament.topicConjunctions);
      console.log(this.currentTournament.availableDate);
      console.log(this.currentTournament.conclusionDate);
      if (
        !this.currentTournament.title ||
        !this.currentTournament.numberOfQuestions ||
        !this.currentTournament.topicConjunctions.length ||
        !this.currentTournament.availableDate ||
        !this.currentTournament.conclusionDate
      ) {
        await this.$store.dispatch(
          'error',
          'Tournament must have a Number of Questions, topic(s), a start and end date'
        );
        return;
      }

      try {
        const result = await RemoteServices.createTournament(
          this.currentTournament
        );
        this.$emit('new-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped></style>
