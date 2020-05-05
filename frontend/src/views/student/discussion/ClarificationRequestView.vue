<template>
    <div>
        <clarification-request
        :clarification-request="clarificationRequest"
        v-on:change-availability="changeAvailability"/>
        <clarification-request-answer :clarification-request="clarificationRequest" />
    </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';
  import ClarificationRequestAnswerComponent from "@/components/discussion/ClarificationRequestAnswerComponent.vue";

  @Component({
    components: {
      'clarification-request': ClarificationRequestComponent,
      'clarification-request-answer': ClarificationRequestAnswerComponent
    }
  })
  export default class ClarificationRequestView extends Vue {
    clarificationRequest!: ClarificationRequest;

    created() {
        this.clarificationRequest = new ClarificationRequest(JSON.parse(this.$route.params.clarificationRequest));
    }

    changeAvailability() {
        this.clarificationRequest.public = this.clarificationRequest.public ? false : true;
    }
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
