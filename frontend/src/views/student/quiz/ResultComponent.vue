<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.optionId === null ? 'unanswered' : '',
      answer.optionId !== null &&
      correctAnswer.correctOptionId === answer.optionId
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <div class="question">
      <span
        @click="decreaseOrder"
        @mouseover="hover = true"
        @mouseleave="hover = false"
        class="square"
      >
        <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
        <span v-else>{{ questionOrder + 1 }}</span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
      <div @click="increaseOrder" class="square">
        <i
          v-if="questionOrder !== questionNumber - 1"
          class="fas fa-chevron-right"
        />
      </div>
    </div>
    <ul class="option-list">
      <li
        v-for="(n, index) in question.options.length"
        :key="index"
        v-bind:class="[
          answer.optionId === question.options[index].optionId ? 'wrong' : '',
          correctAnswer.correctOptionId === question.options[index].optionId
            ? 'correct'
            : '',
          'option'
        ]"
      >
        <i
          v-if="
            correctAnswer.correctOptionId === question.options[index].optionId
          "
          class="fas fa-check option-letter"
        />
        <i
          v-else-if="answer.optionId === question.options[index].optionId"
          class="fas fa-times option-letter"
        />
        <span v-else class="option-letter">{{ optionLetters[index] }}</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        />
      </li>
    </ul>
    <v-container>
      <v-btn v-if="this.answer.questionAnswerDto.hasClarificationRequest" disabled>
        Ask clarification
      </v-btn>
      <v-btn v-else color="primary" @click="newClarificationRequest" data-cy="createClarificationButton">
        Ask clarification
      </v-btn>
      <v-btn class="ml-5" v-if="this.answer.questionAnswerDto.hasClarificationRequest" color="primary" @click="openClarificationRequest" data-cy="seeClarificationButton">
        See My clarification
      </v-btn>
      <v-btn class="ml-5" v-else disabled>
        See My clarification
      </v-btn>
      <v-btn class="ml-5" color="primary" @click="seeOtherClarifications" data-cy="seeOtherClarificationsButton">
        See Other Clarifications
      </v-btn>
    </v-container>
    <create-clarification-request-dialog
      v-if="currentClarificationRequest"
      v-model="createClarificationRequestDialog"
      :clarification-request="currentClarificationRequest"
      :answer="answer"
      v-on:new-clarification-request="onCreateClarificationRequest"
      v-on:close-dialog="onCloseDialog"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import CreateClarificationRequestDialog from '@/views/student/quiz/CreateClarificationRequestDialog.vue';
import { ClarificationRequest } from '@/models/discussion/ClarificationRequest';
import store from '@/store';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'create-clarification-request-dialog': CreateClarificationRequestDialog
  }
})
export default class ResultComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  @Prop(StatementCorrectAnswer) readonly correctAnswer!: StatementCorrectAnswer;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop() readonly questionNumber!: number;
  currentClarificationRequest: ClarificationRequest | null = null;
  clarification: ClarificationRequest | null = null;
  createClarificationRequestDialog: boolean = false;
  hover: boolean = false;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  newClarificationRequest() {
    this.currentClarificationRequest = new ClarificationRequest();
    this.createClarificationRequestDialog = true;
  }

  async onCreateClarificationRequest(clarificationRequest: ClarificationRequest) {
    this.createClarificationRequestDialog = false;
    this.currentClarificationRequest = null;
    this.$emit('update-answers');
  }

  onCloseDialog() {
    this.createClarificationRequestDialog = false;
    this.currentClarificationRequest = null;
  }

  async openClarificationRequest() {
    await this.$store.dispatch('loading');
    if (this.answer.questionAnswerId) {
      try {
        this.clarification = await RemoteServices.getClarificationRequest(this.answer.questionAnswerId);
        await this.$router.push({ name: 'clarification', params: { clarificationRequest: JSON.stringify(this.clarification) } });
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async seeOtherClarifications() {
    await this.$store.dispatch('loading');
    if (this.answer.questionAnswerId) {
      try {
        await this.$router.push({ name: 'questionClarifications', params: {question: JSON.stringify(this.answer.questionAnswerDto.question) }});
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>

<style lang="scss" scoped>
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
