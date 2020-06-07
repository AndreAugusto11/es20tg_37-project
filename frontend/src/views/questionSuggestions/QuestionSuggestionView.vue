<template>
  <div :key="this.update">
    <v-card
      class="mx-auto mt-8 pb-3"
      max-width="1100"
      outlined
      color="#eeeeee"
      v-if="questionSuggestion.justificationDto"
    >
      <v-row align="center" class="spacer ml-5 mt-5 mr-5" no-gutters>
        <v-col md="auto" class="mr-5">
          <v-avatar
            :color="this.getRandomVuetifyColor(this.questionSuggestion.justificationDto.name)"
          >
            <span
              class="white--text headline"
            >{{ this.getNameInitials(this.questionSuggestion.justificationDto.name) }}</span>
          </v-avatar>
        </v-col>

        <v-col sm="3" md="7">
          <h2 class="mb-1 text-left">{{ this.questionSuggestion.justificationDto.name }}</h2>
        </v-col>
      </v-row>

      <v-row align="center" class="spacer" no-gutters>
        <v-col>
          <v-subheader>Justification</v-subheader>
        </v-col>
      </v-row>
      <v-row align="center" class="spacer ml-7 mr-7" no-gutters>
        <v-col class="text-left">
          <span
            class="text-justify"
            v-html="convertMarkDown(questionSuggestion.justificationDto.content, null)"
          />
        </v-col>
      </v-row>

      <v-card-text v-if="questionSuggestion.justificationDto.image">
        <span
          v-html="convertMarkDown('![image][image]', questionSuggestion.justificationDto.image)"
        />
      </v-card-text>

      <v-container v-if="this.editing" grid-list-md fluid>
        <v-layout column wrap>
          <v-flex xs24 sm12 md8>
            <v-textarea
              class="ml-3 mr-3"
              outline
              rows="3"
              v-model="justification.content"
              label="New Justification Content"
              data-cy="Content"
            ></v-textarea>
          </v-flex>
        </v-layout>

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
      </v-container>

      <v-card-actions v-if="this.editing">
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="this.startEditing">close</v-btn>
        <v-btn dark color="blue darken-1" @click="saveJustification">save</v-btn>
      </v-card-actions>
    </v-card>

    <!-- New Card -->

    <v-card
      class="mx-auto mt-8 pb-3"
      max-width="1100"
      outlined
      color="#eeeeee"
      v-if="this.editing && !this.questionSuggestion.justificationDto"
    >
      <v-container v-if="this.editing" grid-list-md fluid>
        <v-layout column wrap>
          <v-flex xs24 sm12 md8>
            <v-textarea
              class="ml-3 mr-3"
              outline
              rows="3"
              v-model="justification.content"
              label="New Justification Content"
              data-cy="Content"
            ></v-textarea>
          </v-flex>
        </v-layout>

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
      </v-container>

      <v-card-actions v-if="this.editing">
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="this.startEditing">close</v-btn>
        <v-btn dark color="blue darken-1" @click="saveJustification">save</v-btn>
      </v-card-actions>
    </v-card>

    <v-card class="mx-auto mb-10 mt-10 pt-2" max-width="1100" outlined>
      <v-row align="center" class="spacer ml-5 mr-5 mt-5" no-gutters>
        <v-col md="auto" class="mr-5">
          <v-avatar :color="this.getRandomVuetifyColor(this.questionSuggestion.name)">
            <span
              class="white--text headline"
            >{{ this.getNameInitials(this.questionSuggestion.name) }}</span>
          </v-avatar>
        </v-col>

        <v-col sm="3" md="7">
          <h2 class="mb-1 text-left">{{ this.questionSuggestion.name }}</h2>
        </v-col>

        <v-col align="right" class="mr-5">
          <v-chip :color="getStatusColor(this.questionSuggestion.status)" small>
            <span>{{ this.questionSuggestion.status }}</span>
          </v-chip>
        </v-col>
      </v-row>

      <v-card-text class="mt-3" v-if="this.questionSuggestion.questionDto.image">
        <span v-html="convertMarkDown('![image][image]', questionSuggestion.questionDto.image)" />
      </v-card-text>

      <v-btn
        v-if="questionSuggestion.status === 'PENDING' && !this.editing"
        absolute
        fab
        top
        right
        color="green"
        class="buttonAccept"
        @click="acceptSuggestion"
      >
        <v-icon class="pl-1" color="white">mdi-check</v-icon>
      </v-btn>

      <v-btn
        v-if="questionSuggestion.status === 'PENDING' && !this.editing"
        absolute
        fab
        top
        right
        color="red"
        @click="this.startEditing"
      >
        <v-icon class="pl-1" color="white">mdi-close</v-icon>
      </v-btn>

      <v-divider v-if="this.questionSuggestion.questionDto.image"></v-divider>

      <v-row align="center" class="spacer" no-gutters>
        <v-col>
          <v-subheader>Question</v-subheader>
        </v-col>
      </v-row>
      <v-row align="center" class="spacer ml-7 mr-7" no-gutters>
        <v-col class="text-left">
          <span
            class="text-justify"
            v-html="convertMarkDown(questionSuggestion.questionDto.content, null)"
          />
        </v-col>
      </v-row>

      <v-card-text class="text-left">
        <v-list>
          <v-list-item
            class="row"
            v-for="option in questionSuggestion.questionDto.options"
            :key="option.sequence"
          >
            <v-list-item-icon>
              <v-tooltip v-if="option.correct" left>
                <template v-slot:activator="{ on }">
                  <v-icon color="green" v-on="on">mdi-check</v-icon>
                </template>
                <span>Correct option</span>
              </v-tooltip>

              <v-tooltip v-else left>
                <template v-slot:activator="{ on }">
                  <v-icon color="red" v-on="on">mdi-close</v-icon>
                </template>
                <span>Incorrect option</span>
              </v-tooltip>
            </v-list-item-icon>
            <v-list-item-content>
              <span v-html="convertMarkDown(option.content, null)" />
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-card-text>
    </v-card>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import QuestionSuggestion from '@/models/management/QuestionSuggestion';
import Image from '@/models/management/Image';
import { getNameInitials } from '@/services/GetNameInitialsService';
import { getRandomVuetifyColor } from '@/services/GetRandomVuetifyColorService';
import RemoteServices from '@/services/RemoteServices';
import Justification from '../../models/management/Justification';

@Component
export default class QuestionSuggestionView extends Vue {
  questionSuggestion!: QuestionSuggestion;
  justification!: Justification;
  editing: boolean = false;
  update: number = 1;
  file!: File;

  async created() {
    this.questionSuggestion = new QuestionSuggestion(
      JSON.parse(this.$route.params.questionSuggestion)
    );
    this.justification = new Justification();
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  getStatusColor(status: string) {
    if (status === 'REJECTED') return 'red';
    else if (status === 'PENDING') return 'orange';
    else return 'green';
  }

  getNameInitials(name: string): string {
    return getNameInitials(name);
  }

  getRandomVuetifyColor(name: string): string {
    return getRandomVuetifyColor(name);
  }

  startEditing() {
    this.editing = this.editing ? false : true;
  }

  constructImage(event: File) {
    this.file = event;
  }

  async acceptSuggestion() {
    try {
      if (this.questionSuggestion.id) {
        await RemoteServices.acceptQuestionSuggestion(
          this.questionSuggestion.id
        );
        this.questionSuggestion.status = 'ACCEPTED';
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.update++;
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

          if (this.questionSuggestion.justificationDto) {
            this.questionSuggestion.justificationDto.content = this.justification.content;
          }
          else {
            this.questionSuggestion.justificationDto = this.justification;
            this.questionSuggestion.justificationDto.name = this.$store.getters.getUser.name
          }        

          this.questionSuggestion.status = 'REJECTED';

          if (this.file) {
            let url = await RemoteServices.uploadImageToJustification(
              this.questionSuggestion.id,
              this.file
            );
            let image = new Image();
            image.url = url;

            this.questionSuggestion.justificationDto.image = image;
          } else {
            this.questionSuggestion.justificationDto.image = null;
          }
          this.update++;
          this.editing = false;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss">
img {
  max-width: 95%;
  margin-left: auto;
  margin-right: auto;
}

.v-application p {
  margin-bottom: 0 !important;
}

.buttonAccept {
  margin-right: 6%;
}
</style>