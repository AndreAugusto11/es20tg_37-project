<template>
    <clarification-request :clarification-request="clarificationRequest" />
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
  import ClarificationRequestComponent from '@/components/discussion/ClarificationRequestComponent.vue';

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
  }
</script>

<style lang="scss" scoped>
    .post-text {
        text-align: left !important;
    }
</style>
