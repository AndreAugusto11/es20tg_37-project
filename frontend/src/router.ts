import Vue from 'vue';
import Router from 'vue-router';
import Store from '@/store';

import LoginView from '@/views/LoginView.vue';
import CourseSelectionView from '@/views/CourseSelectionView.vue';

import HomeView from '@/views/HomeView.vue';
import ManagementView from '@/views/teacher/ManagementView.vue';
import TeacherStatsView from '@/views/teacher/TeacherStatsView.vue';
import QuestionsView from '@/views/teacher/questions/QuestionsView.vue';
import TopicsView from '@/views/teacher/TopicsView.vue';
import QuizzesView from '@/views/teacher/quizzes/QuizzesView.vue';
import StudentView from '@/views/student/StudentView.vue';
import TournamentsView from '@/views/student/tournaments/TournamentsView.vue';
import EnrolledTournamentsView from '@/views/student/tournaments/EnrolledTournamentsView.vue';
import CreateTournamentsView from '@/views/student/tournaments/CreateTournamentsView.vue';
import ListQuestionSuggestionsView from './views/questionSuggestions/ListQuestionSuggestionsView.vue';
import QuestionSuggestionView from './views/questionSuggestions/QuestionSuggestionView.vue'
import AvailableQuizzesView from '@/views/student/AvailableQuizzesView.vue';
import SolvedQuizzesView from '@/views/student/SolvedQuizzesView.vue';
import QuizView from '@/views/student/quiz/QuizView.vue';
import ResultsView from '@/views/student/quiz/ResultsView.vue';
import StatsView from '@/views/student/StatsView.vue';
import ScanView from '@/views/student/ScanView.vue';

import AdminManagementView from '@/views/admin/AdminManagementView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import ImpExpView from '@/views/teacher/impexp/ImpExpView.vue';
import AssessmentsView from '@/views/teacher/assessments/AssessmentsView.vue';
import CreateQuizzesView from '@/views/student/CreateQuizzesView.vue';
import CoursesView from '@/views/admin/Courses/CoursesView.vue';
import ListClarificationRequestsView from '@/views/discussion/ListClarificationRequestsView.vue';
import QuestionClarificationRequestsView from '@/views/student/discussion/QuestionClarificationRequestsView.vue';
import ClarificationRequestView from '@/views/discussion/ClarificationRequestView.vue';

Vue.use(Router);

let router = new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: process.env.VUE_APP_NAME, requiredAuth: 'None' }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Login',
        requiredAuth: 'None'
      }
    },
    {
      path: '/courses',
      name: 'courses',
      component: CourseSelectionView,
      meta: {
        title: process.env.VUE_APP_NAME + ' - Course Selection',
        requiredAuth: 'None'
      }
    },
    {
      path: '/management',
      name: 'management',
      component: ManagementView,
      children: [
        {
          path: 'questions',
          name: 'questions-management',
          component: QuestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'suggestions',
          name: 'student-suggestions-management',
          component: ListQuestionSuggestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'suggestion',
          name: 'suggestionTeacher',
          component: QuestionSuggestionView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - View Question Suggestion',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'topics',
          name: 'topics-management',
          component: TopicsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'quizzes',
          name: 'quizzes-management',
          component: QuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quizzes',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'assessments',
          name: 'assessments-management',
          component: AssessmentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Assessment Topics',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'discussion',
          name: 'clarificationRequests-management',
          component: ListClarificationRequestsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Clarification Requests',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'discussionQuestion',
          name: 'discussionQuestion',
          component: ClarificationRequestView,
          props: true,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussion Question',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'stats',
          name: 'teacherStats',
          component: TeacherStatsView,
          props: true,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Students Stats',
            requiredAuth: 'Teacher'
          }
        },
        {
          path: 'impexp',
          name: 'impexp-management',
          component: ImpExpView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - ImpExp',
            requiredAuth: 'Teacher'
          }
        }
      ]
    },
    {
      path: '/student',
      name: 'student',
      component: StudentView,
      children: [
        {
          path: 'suggestions',
          name: 'create-suggestions',
          component: ListQuestionSuggestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Questions',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'suggestion',
          name: 'suggestionStudent',
          component: QuestionSuggestionView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - View Question Suggestion',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'available',
          name: 'available-quizzes',
          component: AvailableQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Available Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'create',
          name: 'create-quizzes',
          component: CreateQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'solved',
          name: 'solved-quizzes',
          component: SolvedQuizzesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Solved Quizzes',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'quiz',
          name: 'solve-quiz',
          component: QuizView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Quiz',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'results',
          name: 'quiz-results',
          component: ResultsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Results',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'stats',
          name: 'stats',
          component: StatsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Stats',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'discussion',
          name: 'discussion',
          component: ListClarificationRequestsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Discussion',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'clarification',
          name: 'clarification',
          component: ClarificationRequestView,
          props: true,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Clarification',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'questionClarifications',
          name: 'questionClarifications',
          component: QuestionClarificationRequestsView,
          props: true,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Question Clarifications',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'scan',
          name: 'scan',
          component: ScanView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Scan',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'tournaments',
          name: 'tournaments',
          component: TournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'enrolled',
          name: 'enrolled',
          component: EnrolledTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Enrolled Tournaments',
            requiredAuth: 'Student'
          }
        },
        {
          path: 'createTournament',
          name: 'createTournament',
          component: CreateTournamentsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Create Tournaments',
            requiredAuth: 'Student'
          }
        }
      ]
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminManagementView,
      children: [
        {
          path: 'courses',
          name: 'courseAdmin',
          component: CoursesView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Courses',
            requiredAuth: 'Admin'
          }
        },
        {
          path: 'suggestions',
          name: 'suggestionsAdmin',
          component: ListQuestionSuggestionsView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - Manage Question Suggestions',
            requiredAuth: 'Admin'
          },
          children: [
            
          ]
        },
        {
          path: 'suggestion',
          name: 'suggestionAdmin',
          component: QuestionSuggestionView,
          meta: {
            title: process.env.VUE_APP_NAME + ' - View Question Suggestion',
            requiredAuth: 'Admin'
          }
        }
      ]
    },
    {
      path: '**',
      name: 'not-found',
      component: NotFoundView,
      meta: { title: 'Page Not Found', requiredAuth: 'None' }
    }
  ]
});

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiredAuth == 'None') {
    next();
  } else if (to.meta.requiredAuth == 'Admin' && Store.getters.isAdmin) {
    next();
  } else if (to.meta.requiredAuth == 'Teacher' && Store.getters.isTeacher) {
    next();
  } else if (to.meta.requiredAuth == 'Student' && Store.getters.isStudent) {
    next();
  } else {
    next('/');
  }
});

router.afterEach(async (to, from) => {
  document.title = to.meta.title;
  await Store.dispatch('clearLoading');
});

export default router;
