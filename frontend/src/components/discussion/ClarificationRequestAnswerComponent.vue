<template>
    <v-card
            v-if="this.clarificationRequestAnswer.content"
            class="mx-auto mt-10"
            max-width="1000"
            outlined
    >

        <v-row
                align="center"
                class="spacer ml-5 mt-5"
                no-gutters
        >
            <v-col md="auto" class="mr-5">
                <v-avatar :color="this.getRandomVuetifyColor(this.clarificationRequestAnswer.name)">
                    <span class="white--text headline">{{ this.getNameInitials(this.clarificationRequestAnswer.name) }}</span>
                </v-avatar>
            </v-col>
            <v-col>
                <h2 class="mb-1 post-text">{{ this.clarificationRequestAnswer.name }}</h2>
            </v-col>
            <v-col align="right" class="mr-5">
                <v-tooltip v-if="this.clarificationRequestAnswer.type === 'TEACHER_ANSWER'" bottom>
                    <template v-slot:activator="{ on }">
                        <v-avatar v-on="on" tile color="yellow darken-2" size="36" style="border-radius: 5px;">
                            <span class="white--text headline">T</span>
                        </v-avatar>
                    </template>
                    <span>Teacher's Reply</span>
                </v-tooltip>
                <v-tooltip v-else-if="this.clarificationRequestAnswer.type === 'STUDENT_ANSWER'" bottom>
                    <template v-slot:activator="{ on }">
                        <v-avatar v-on="on" tile color="green" size="36" style="border-radius: 5px;">
                            <span class="white--text headline">S</span>
                        </v-avatar>
                    </template>
                    <span>Student's Reply</span>
                </v-tooltip>
            </v-col>
        </v-row>

        <v-row
                align="center"
                class="spacer ml-5"
                no-gutters
        >
            <v-col md="auto" class="mr-5">
                <v-avatar :color="this.getRandomVuetifyColor(this.clarificationRequestAnswer.name)" style="opacity: 0;">
                    <span class="white--text headline">{{ this.getNameInitials(this.clarificationRequestAnswer.name) }}</span>
                </v-avatar>
            </v-col>
            <v-col class="post-text">
                <span v-html="convertMarkDown(this.clarificationRequestAnswer.content, null)" />
            </v-col>
        </v-row>
        <v-row
                align="center"
        >
          <v-col v-if="this.clarificationRequestAnswer.image">
              <span v-html="convertMarkDown('![image][image]', this.clarificationRequestAnswer.image)"/>
          </v-col>
        </v-row>

        <v-row
                align="center"
                class="spacer ml-5"
                no-gutters
        >
            <v-col align="right" class="mr-5" style="color: grey; font-size: 14px;">
                <span v-html="convertMarkDown(this.getTimeDiff(this.clarificationRequestAnswer.creationDate), null)" />
            </v-col>
        </v-row>
    </v-card>
</template>

<script lang="ts">
    import { Component, Prop, Vue } from 'vue-property-decorator';
    import { convertMarkDown } from '@/services/ConvertMarkdownService';
    import { getNameInitials } from '@/services/GetNameInitialsService';
    import { getRandomVuetifyColor } from '@/services/GetRandomVuetifyColorService';
    import Image from '@/models/management/Image';
    import { ClarificationRequestAnswer } from '@/models/discussion/ClarificationRequestAnswer';

    @Component
    export default class ClarificationRequestAnswerComponent extends Vue {
        @Prop(ClarificationRequestAnswer) readonly clarificationRequestAnswer!: ClarificationRequestAnswer;

      convertMarkDown(text: string, image: Image | null = null): string {
            return convertMarkDown(text, image);
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
          return "";

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

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }

    img {
        max-width: 900px;
        margin-left: auto;
        margin-right: auto;
    }
</style>
