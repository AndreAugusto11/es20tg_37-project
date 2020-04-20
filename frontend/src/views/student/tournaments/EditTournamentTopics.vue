<template>
  <v-form>
    <v-autocomplete
      v-model="tournamentTopics"
      :items="topics"
      multiple
      return-object
      item-text="name"
      item-value="name"
      @change="saveTopics"
    >
      <template v-slot:selection="data">
        <v-chip
          v-bind="data.attrs"
          :input-value="data.selected"
          close
          @click="data.select"
          @click:close="removeTopic(data.item)"
        >
          {{ data.item.name }}
        </v-chip>
      </template>
      <template v-slot:item="data">
        <v-list-item-content>
          <v-list-item-title v-html="data.item.name" />
        </v-list-item-content>
      </template>
    </v-autocomplete>
  </v-form>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';
import {Tournament} from '@/models/tournaments/Tournament';

@Component
export default class EditTournamentTopics extends Vue {
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];

  tournamentTopics: Topic[] = JSON.parse(JSON.stringify(this.tournament.topics));

  async saveTopics() {
    this.$emit(
      'tournament-changed-topics',
      this.tournamentTopics
    );
  }

  removeTopic(topic: Topic) {
    this.tournamentTopics = this.tournamentTopics.filter(
      element => element.id != topic.id
    );
    this.saveTopics();
  }
}
</script>
