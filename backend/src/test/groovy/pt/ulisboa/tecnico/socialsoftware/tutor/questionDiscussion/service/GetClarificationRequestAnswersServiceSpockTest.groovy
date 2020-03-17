package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequestAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_NOT_DEFINED;

@DataJpaTest
class GetClarificationRequestAnswersServiceSpockTest extends Specification {


    public static final String CLARIFICATION_ANSWER_CONTENT = "TestAnswer"
    public static final String USERNAME_TEACHER = "username_teacher"
    public static final String USERNAME_STUDENT = "username_student"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    QuestionDiscussionService questionDiscussionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    ClarificationRequestAnswerRepository clarificationRequestAnswerRepository

    def user_teacher
    def user_student
    def course
    def courseExecution
    def question
    def quizQuestion
    def quizAnswer
    def quiz
    def questionAnswer

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user_teacher = new User("name", USERNAME_TEACHER, 1, User.Role.TEACHER)
        user_student = new User("name", USERNAME_STUDENT, 2, User.Role.STUDENT)
        user_teacher.getCourseExecutions().add(courseExecution)
        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_teacher)
        userRepository.save(user_teacher)
        userRepository.save(user_student)

        question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setKey(1)
        questionRepository.save(question)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(user_student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
    }

    def "clarification request answers are successful returned"() {
        given: "3 clarification requests"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest3 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest1)
        clarificationRequestRepository.save(clarificationRequest2)
        clarificationRequestRepository.save(clarificationRequest3)

        and: "7 clarification requests answers associated"
        def clarificationRequestAnswer1 = new ClarificationRequestAnswer(clarificationRequest1, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer2 = new ClarificationRequestAnswer(clarificationRequest1, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer3 = new ClarificationRequestAnswer(clarificationRequest1, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer4 = new ClarificationRequestAnswer(clarificationRequest2, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer5 = new ClarificationRequestAnswer(clarificationRequest2, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer6 = new ClarificationRequestAnswer(clarificationRequest3, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer7 = new ClarificationRequestAnswer(clarificationRequest3, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer1)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer2)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer3)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer4)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer5)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer6)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer7)

        when:
        def result = questionDiscussionService.getClarificationRequestAnswers(user_student.getUsername(), courseExecution.getId());

        then:
        def example = result.get(0)
        result.size() == 7
        example.getId() != null
        example.getUsername() == user_teacher.getUsername()
        example.getType().equals(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        example.getContent() == CLARIFICATION_ANSWER_CONTENT
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }
    }
}
