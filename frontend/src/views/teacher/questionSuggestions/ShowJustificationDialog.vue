<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="50%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-textarea
                outline
                rows="4"
                v-model="justification.content"
                label="Content"
                data-cy="Content"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>

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

      <v-card-actions>
        <v-spacer />

        <v-btn dark color="blue darken-1" @click="closeJustificationDialog">close</v-btn>

        <v-btn
          dark
          color="blue darken-1"
          @click="saveJustification"
          data-cy="saveJustification"
        >save</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '../../../models/management/QuestionSuggestion';
import Justification from '@/models/management/Justification';
import Image from '@/models/management/Image';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ShowSuggestionDialog extends Vue {
  @Prop({ type: QuestionSuggestion, required: true })
  readonly questionSuggestion!: QuestionSuggestion;
  @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

  justification!: Justification;
  file!: File;

  async created() {
    this.justification = new Justification();
  }

  async constructImage(event: File) {
    this.file = event;
  }

  closeJustificationDialog() {
    this.$emit('close-dialog');
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  async saveJustification() {
    if (this.justification && !this.justification.content) {
      await this.$store.dispatch('error', 'Justification must have content');
    } else {
      try {
        if (this.questionSuggestion.id) {
          await RemoteServices.rejectQuestionSuggestion(
            this.questionSuggestion.id,
            this.justification
          );

          this.questionSuggestion.justificationDto = this.justification;

          if (this.file) {
            let url = await RemoteServices.uploadImageToJustification(
              this.questionSuggestion.id,
              this.file
            );
            let image = new Image();
            image.url = url;

            this.questionSuggestion.justificationDto.image = image;
            this.$emit('reject-suggestion', {
              content: this.justification.content,
              image: image
            });
          } else {
            this.questionSuggestion.justificationDto.image = null;
            this.$emit('reject-suggestion', {
              content: this.justification.content,
              image: null
            });
          }
        } else {
          this.$emit('reject-suggestion', {
            content: this.justification.content,
            image: null
          });
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
