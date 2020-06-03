<template>
    <v-card
            :key="this.clarificationRequest.public"
            class="mx-auto mt-10"
            max-width="1000"
            outlined
    >
        <v-row
                align="center"
                class="spacer ml-5 mr-5 mt-5"
                no-gutters
        >
            <v-col md="auto" class="mr-5">
                <v-avatar :color="this.getRandomVuetifyColor(this.clarificationRequest.name)">
                    <span class="white--text headline">{{ this.getNameInitials(this.clarificationRequest.name) }}</span>
                </v-avatar>
            </v-col>

            <v-col v-if="this.$store.getters.isTeacher"
                    sm="3"
                    md="7"
            >
                <h2 class="mb-1 post-text">{{ this.clarificationRequest.name }}</h2>
            </v-col>

            <v-col v-if="this.$store.getters.isStudent"
                    sm="3"
                    md="7"
            >
                <h2 class="mb-1 post-text">{{ this.clarificationRequest.name }}</h2>
            </v-col>


            <v-col align="right" class="mr-5">
                <v-chip :color="getStatusColor(this.clarificationRequest.status)" small>
                    <span>{{ this.clarificationRequest.status }}</span>
                </v-chip>
            </v-col>


            <v-col v-if="this.$store.getters.isTeacher">
                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="green" v-on="on">mdi-eye</v-icon>
                    </template>
                    <span>This discussion is public</span>
                </v-tooltip>

                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="red" v-on="on">mdi-eye-off</v-icon>
                    </template>
                    <span>This discussion is private</span>
                </v-tooltip>

                <v-icon big>mdi-arrow-right-bold</v-icon>

                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn class="mx-2" fab color="red" @click="changePrivatePublic()" data-cy="ButtonToPrivate">
                            <v-icon class="mr-2" color="white" v-on="on">mdi-eye-off</v-icon>
                        </v-btn>
                    </template>
                    <span>Click to make private</span>
                </v-tooltip>

                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn class="mx-2" fab color="green" @click="changePrivatePublic()" data-cy="ButtonToPublic">
                            <v-icon class="mr-2" color="white" v-on="on">mdi-eye</v-icon>
                        </v-btn>
                    </template>
                    <span>Click to make public</span>
                </v-tooltip>
            </v-col>


            <v-col align="right" md="auto" v-if="this.$store.getters.isStudent">
                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="green" v-on="on">mdi-eye</v-icon>
                    </template>
                    <span>This discussion is public</span>
                </v-tooltip>
    
                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="red" v-on="on">mdi-eye-off</v-icon>
                    </template>
                    <span>This discussion is private</span>
                </v-tooltip>
            </v-col>
        </v-row>
        <v-row
                align="center"
                class="spacer ml-5"
                no-gutters
        >
            <v-col md="auto" class="mr-5">
                <v-avatar :color="this.getRandomVuetifyColor(this.clarificationRequest.name)" style="opacity: 0;">
                    <span class="white--text headline">{{ this.getNameInitials(this.clarificationRequest.name) }}</span>
                </v-avatar>
            </v-col>
            <v-col class="post-text">
                <span v-html="convertMarkDown(this.clarificationRequest.content, null)" />
            </v-col>
        </v-row>
        <v-row
                align="center"
        >
            <v-col>
                <span v-html="convertMarkDown('![image][image]', this.clarificationRequest.image)"/>
            </v-col>
        </v-row>
        <v-divider></v-divider>
        <v-row
                align="center"
                class="spacer"
                no-gutters
        >
            <v-col>
                <v-subheader>Question</v-subheader>
            </v-col>
        </v-row>
        <v-row
                align="center"
                class="spacer ml-5"
                no-gutters
        >
            <v-col class="post-text">
                <span class="post-text" v-html="convertMarkDown(this.clarificationRequest.questionAnswerDto.question.content, this.clarificationRequest.questionAnswerDto.question.image)" />
            </v-col>
        </v-row>
        <v-list>
            <v-list-item class="post-text" v-for="item in this.clarificationRequest.questionAnswerDto.question.options" :key="item.content">
                <v-list-item-icon>
                    <v-tooltip v-if="item.correct" left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="green" v-on="on">mdi-check</v-icon>
                        </template>
                        <span>Correct option</span>
                    </v-tooltip>
                    <v-tooltip v-else-if="chosen(item.content)" left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="yellow darken-3" v-on="on">mdi-help</v-icon>
                        </template>
                        <span>Student's option</span>
                    </v-tooltip>
                    <v-tooltip v-else left>
                        <template v-slot:activator="{ on }">
                            <v-icon color="red" v-on="on">mdi-close</v-icon>
                        </template>
                        <span>Incorrect option</span>
                    </v-tooltip>
                </v-list-item-icon>

                <v-list-item-content class="pt-5">
                    <span v-html="convertMarkDown(item.content, null)" />
                </v-list-item-content>
            </v-list-item>
        </v-list>
        <v-row
            align="center"
            class="spacer ml-5"
            no-gutters
        >
            <v-col align="right" class="mr-5" style="color: grey; font-size: 14px;">
                <span v-html="convertMarkDown(this.getTimeDiff(this.clarificationRequest.creationDate), null)" />
            </v-col>
        </v-row>
    </v-card>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import RemoteServices from '@/services/RemoteServices';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import { getNameInitials } from '@/services/GetNameInitialsService';
  import { getRandomVuetifyColor } from '@/services/GetRandomVuetifyColorService';

  @Component
  export default class ClarificationRequestComponent extends Vue {
    @Prop(ClarificationRequest) clarificationRequest!: ClarificationRequest;

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }

    getStatusColor(status: string) {
      if (status === 'CLOSED') return 'red';
      else if (status === 'ANSWERED') return 'yellow';
      else return 'green'
    }

    chosen(content: string) {
      return content === this.clarificationRequest.questionAnswerDto.option.content;
    }

    async changePrivatePublic() {
        var result;
        
        try {
            if (this.clarificationRequest.public && this.clarificationRequest.id != null)
                result = await RemoteServices.makeClarificationRequestPrivate(this.clarificationRequest.id);

            if (!this.clarificationRequest.public && this.clarificationRequest.id != null)
                result = await RemoteServices.makeClarificationRequestPublic(this.clarificationRequest.id);

            if (result != null)
                this.clarificationRequest = result;

        } catch (error) {
          await this.$store.dispatch('error', error);
        }
    }

    getNameInitials(name: string): string {
      return getNameInitials(name);
    }

    getRandomVuetifyColor(name: string): string {
      return getRandomVuetifyColor(name);
    }

          getTimeDiff(dateTimeString: string) {
        let datetime: number;
        let now: number;
        let milisec_diff: number;
        let days: number;
        let date_diff: Date;

        datetime = new Date(dateTimeString).getTime();
        now = new Date().getTime();

        if (isNaN(datetime))
          return '';

        if (datetime < now) {
          milisec_diff = now - datetime;
        }
        else {
          milisec_diff = datetime - now;
        }

        days = Math.floor(milisec_diff / 1000 / 60 / (60 * 24));

        date_diff = new Date(milisec_diff);

        if (days > 0) {
          return days + ' Days ago';
        }
        else if ((date_diff.getHours() - 1) > 0) {
          if ((date_diff.getHours() - 1) > 1) {
            return (date_diff.getHours() - 1) + ' Hours ago';
          }
          else {
            return (date_diff.getHours() - 1) + ' Hour ago';
          }
        }
        else if (date_diff.getMinutes() > 0) {
          if (date_diff.getMinutes() > 1) {
            return date_diff.getMinutes() + ' Minutes ago';
          }
          else {
            return date_diff.getMinutes() + ' Minute ago';
          }
        }
        else {
          return date_diff.getSeconds() + ' Seconds ago';
        }
      }
  }
</script>

<style lang="scss">
    .post-text {
        text-align: left !important;
    }

    img {
        max-width: 900px;
        margin-left: auto;
        margin-right: auto;
    }
</style>
