<template>
  <v-dialog
    :value="dialog"
    v-on:click:outside="closeSuggestionDialog"
    @keydown.esc="closeSuggestionDialog"
    max-width="65%"
  >
    <v-card>
      <v-card-text class="text-left">
        <show-questionSuggestion :questionSuggestion="questionSuggestion" />
      </v-card-text>

      <v-card-actions v-if="!rejected">
        <v-btn v-if="questionSuggestion.status === 'PENDING'"
          dark
          color="green darken-1"
          @click="acceptSuggestionDialog"
          data-cy="acceptQuestion"
          >Accept</v-btn
        >
        <v-btn v-if="questionSuggestion.status === 'PENDING'"
          dark
          color="red darken-1"
          @click="rejectSuggestionDialog"
          data-cy="rejectQuestion"
          >Reject</v-btn
        >
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
          data-cy="justification_text"
        ></v-textarea>
        <template>
          <v-file-input
          class="pr-3"
          label="File input"
          show-size
          outlined
          counter
          dense
          @change="constructImage($event)"
          accept="image/*"
          />
        </template>
      </v-card-text>

      <v-card-actions v-if="rejected">
        <v-spacer />

        <v-btn dark color="blue darken-1" @click="closeJustificationDialog">
          close
        </v-btn>

        <v-btn
          dark
          color="blue darken-1"
          @click="saveJustification"
          data-cy="saveJustification"
        >
          save
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-card class="mt-5" v-if="questionSuggestion.justificationDto">
      <v-card-title>
        <span class="headline">Justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        <span v-html="convertMarkDown(questionSuggestion.justificationDto.content, null)" />
      </v-card-text>

      <v-card-text class="text-left" v-if="questionSuggestion.justificationDto.image">
        <span v-html="convertMarkDown('![image][image]', questionSuggestion.justificationDto.image)" />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '../../../models/management/QuestionSuggestion';
import ShowQuestionSuggestion from '@/views/student/questionSuggestion/ShowQuestionSuggestion.vue';
import Justification from '@/models/management/Justification';
import Image from '@/models/management/Image';
import RemoteServices from '@/services/RemoteServices';

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
  file!: File

  async created() {
    this.justification = new Justification();
  }

  async constructImage(event: File) {
    this.file = event;
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

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async saveJustification() {
    if (this.justification && !this.justification.content) {
      await this.$store.dispatch('error', 'Justification must have content');
    } else {
      try {
        if (this.questionSuggestion.id && this.questionSuggestion.justificationDto) {
          let questionSuggestion = await RemoteServices.rejectQuestionSuggestion(this.questionSuggestion.id, this.justification);

          if (this.file) {
            let url = await RemoteServices.uploadImageToJustification(this.questionSuggestion.id, this.file);
            let image = new Image();
            image.url = url;
            console.log("URL");
            console.log(url);
            
            this.questionSuggestion.justificationDto.image = image
            this.$emit('reject-suggestion', {'content': this.justification.content, 'image': image});
          }
          else {
            this.questionSuggestion.justificationDto.image = null;
            this.$emit('reject-suggestion', {'content': this.justification.content, 'image': null});
          }
        }
        else {
          this.$emit('reject-suggestion', {'content': this.justification.content, 'image': null});
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
