<template>
  <v-dialog
      :value="dialog"
      v-on:click:outside="closeSuggestionDialog"
      @keydown.esc="closeSuggestionDialog"
      max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ questionSuggestion.questionDto.title }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-questionSuggestion :questionSuggestion="questionSuggestion" />
      </v-card-text>

      <v-card-actions v-if="!rejected">
        <v-btn dark color="green darken-1" @click="acceptSuggestionDialog" data-cy="acceptQuestion"
        >Accept</v-btn>
        <v-btn dark color="red darken-1" @click="rejectSuggestionDialog"
        >Reject</v-btn>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="closeSuggestionDialog"
        >close</v-btn
        >
      </v-card-actions>

      <v-card-text v-if="rejected">
        <v-textarea
          outline
          rows="2"
          label="Justification"
          v-model="justification.content"
        ></v-textarea>
      </v-card-text>

      <v-card-actions v-if="rejected">
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="saveJustification"
        >save</v-btn>
        <v-btn dark color="blue darken-1" @click="closeJustificationDialog"
        >close</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import QuestionSuggestion from '../../../models/management/QuestionSuggestion';
  import ShowQuestionSuggestion from '@/views/student/questionSuggestion/ShowQuestionSuggestion.vue';
  import Justification from '@/models/management/Justification';

  @Component({
    components: {
      'show-questionSuggestion': ShowQuestionSuggestion
    }
  })
  export default class ShowSuggestionDialog extends Vue {
    @Prop({ type: QuestionSuggestion, required: true })
    readonly questionSuggestion!: QuestionSuggestion;
    @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;
    @Prop({ type: Boolean, required: true }) rejected!: boolean;

    justification!: Justification;

    async created() {
      this.justification = new Justification();
    }

    closeSuggestionDialog() {
      if (this.rejected) this.rejected = false;
      this.justification.content = '';
      this.$emit('close-dialog');
    }

    acceptSuggestionDialog() {
      this.$emit('accept-suggestion');
    }

    rejectSuggestionDialog() {
      this.rejected = true;
    }

    closeJustificationDialog() {
      this.rejected = false;
    }

    async saveJustification() {
      if (
        this.justification && (!this.justification.content)
      ) {
        await this.$store.dispatch(
          'error',
          'Justification must have content'
        );
        return;
      } else {
        try {
          this.$emit('reject-suggestion', this.justification);
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }
  }
</script>