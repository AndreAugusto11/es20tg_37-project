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
        </v-row>

        <v-row
                align="center"
                class="spacer ml-5 mb-5"
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
    }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
