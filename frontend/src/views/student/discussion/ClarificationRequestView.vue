<template>
    <div>
        <clarification-request :clarification-request="clarificationRequest" />
        <v-card
                v-if="this.clarificationRequest.clarificationRequestAnswerDto.content"
                class="mx-auto mt-10"
                max-width="1000"
                outlined
        >

            <v-row
                    align="center"
                    class="spacer ml-5 mt-5"
                    no-gutters
            >
                <v-col
                        sm="2"
                        md="9"
                >
                    <h2 class="mb-1 post-text">{{ this.clarificationRequest.clarificationRequestAnswerDto.name }}</h2>
                </v-col>
            </v-row>

            <v-row
                    align="center"
                    class="spacer ml-5 mb-5"
                    no-gutters
            >
                <v-col class="post-text">
                    <span v-html="convertMarkDownNoFigure(this.clarificationRequest.clarificationRequestAnswerDto.content, null)" />
                </v-col>
            </v-row>
        </v-card>
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';
  import Image from "@/models/management/Image";

  @Component({
    components: {
      'clarification-request': ClarificationRequestComponent
    }
  })
  export default class ClarificationRequestView extends Vue {
    clarificationRequest!: ClarificationRequest;

    created() {
        this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
    }

      convertMarkDownNoFigure(text: string, image: Image | null = null): string {
          return convertMarkDownNoFigure(text, image);
      }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
