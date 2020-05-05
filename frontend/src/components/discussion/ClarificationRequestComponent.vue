<template>
    <v-card
            class="mx-auto mt-10"
            max-width="1000"
            outlined
    >
        <v-row
                align="center"
                class="spacer ml-5 mr-5 mt-5"
                no-gutters
        >
            <v-col v-if="this.$store.getters.isTeacher"
                    sm="2"
                    md="8"
            >
                <h2 class="mb-1 post-text">{{ this.clarificationRequest.name }}</h2>
            </v-col>

            <v-col v-if="this.$store.getters.isStudent"
                    sm="2"
                    md="10"
            >
                <h2 class="mb-1 post-text">{{ this.clarificationRequest.name }}</h2>
            </v-col>

            <v-col>
                <v-chip :color="getStatusColor(this.clarificationRequest.status)" small>
                    <span>{{ this.clarificationRequest.status }}</span>
                </v-chip>
            </v-col>

            <v-col v-if="this.$store.getters.isTeacher">
                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="green" v-on="on" data-cy="buttonPublic">fas fa-lock-open</v-icon>
                    </template>
                    <span>This discussion is public</span>
                </v-tooltip>

                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="red" v-on="on" data-cy="buttonPrivate">fas fa-lock</v-icon>
                    </template>
                    <span>This discussion is private</span>
                </v-tooltip>

                <v-icon big>mdi-arrow-right-bold</v-icon>

                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn class="mx-2" fab dark color="red" @click="changePrivatePublic()">
                            <v-icon class="mr-2" color="white" v-on="on" data-cy="buttonPrivate">fas fa-lock</v-icon>
                        </v-btn>
                    </template>
                    <span>Click to make private</span>
                </v-tooltip>

                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-btn class="mx-2" fab dark color="green" @click="changePrivatePublic()">
                            <v-icon class="mr-2" color="white" v-on="on" data-cy="buttonPublic">fas fa-lock-open</v-icon>
                        </v-btn>
                    </template>
                    <span>Click to make public</span>
                </v-tooltip>
            </v-col>

            <v-col v-if="this.$store.getters.isStudent">
                <v-tooltip v-if="this.clarificationRequest.public" bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="green" v-on="on" data-cy="iconPublic">fas fa-lock-open</v-icon>
                    </template>
                    <span>This discussion is public</span>
                </v-tooltip>
    
                <v-tooltip v-else bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon class="mr-2" color="red" v-on="on" data-cy="iconPrivate">fas fa-lock</v-icon>
                    </template>
                    <span>This discussion is private</span>
                </v-tooltip>
            </v-col>
        </v-row>
        <v-row
                align="center"
                class="spacer ml-5 mb-5"
                no-gutters
        >
            <v-col class="post-text">
                <span v-html="convertMarkDown(this.clarificationRequest.content, null)" />
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
                <span class="post-text" v-html="convertMarkDown(this.clarificationRequest.questionAnswerDto.question.content, null)" />
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

                <v-list-item-content>
                    <span v-html="convertMarkDown(item.content, null)" />
                </v-list-item-content>
            </v-list-item>
        </v-list>
        <!-- <v-list>
            <v-list-item class="post-text" v-for="item in this.clarificationRequest.questionAnswerDto.question.options" :key="item.content">
                <v-tooltip left>
                    <template v-slot:activator="{ on }">
                        <v-list-item-icon>
                            <v-icon v-if="item.correct" color="green" v-on="on">mdi-check</v-icon>
                            <v-icon v-else-if="chosen(item.content)" color="yellow" v-on="on">mdi-help</v-icon>
                            <v-icon v-else color="red" v-on="on">mdi-close</v-icon>
                        </v-list-item-icon>

                        <v-list-item-content>
                            <span v-on="on" v-html="convertMarkDownNoFigure(item.content, null)" />
                        </v-list-item-content>
                    </template>
                    <span v-if="item.correct">Correct option</span>
                    <span v-else-if="chosen(item.content)">Student's option</span>
                    <span v-else >Incorrect option</span>
                </v-tooltip>
            </v-list-item>
        </v-list> -->
    </v-card>
</template>

<script lang="ts">
  import { Component, Prop, Vue } from 'vue-property-decorator';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';
  import RemoteServices from '@/services/RemoteServices';
  import Image from '@/models/management/Image';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import ResultComponent from '../../views/student/quiz/ResultComponent.vue';

  @Component
  export default class ClarificationRequestComponent extends Vue {
    @Prop(ClarificationRequest) clarificationRequest!: ClarificationRequest;

    convertMarkDown(text: string, image: Image | null = null): string {
      return convertMarkDown(text, image);
    }

    getStatusColor(status: string) {
      if (status === 'CLOSED') return 'red';
      else return 'green';
    }

    chosen(content: string) {
      return content === this.clarificationRequest.questionAnswerDto.option.content;
    }

    async changePrivatePublic() {
        var result;
        
        if (this.clarificationRequest.public && this.clarificationRequest.id != null)
            result = await RemoteServices.makeClarificationRequestPrivate(this.clarificationRequest.id);

        if (!this.clarificationRequest.public && this.clarificationRequest.id != null)
            result = await RemoteServices.makeClarificationRequestPublic(this.clarificationRequest.id);

        this.$emit('change-availability');
    }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
