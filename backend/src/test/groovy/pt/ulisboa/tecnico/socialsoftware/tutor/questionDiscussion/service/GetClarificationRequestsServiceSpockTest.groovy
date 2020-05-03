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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetClarificationRequestsServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "clarification request content"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TIME_TAKEN = 1234
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0

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
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    def user_student
    def user_teacher
    def course
    def courseExecution
    def question
    def quizQuestion
    def option
    def quizAnswer
    def quiz
    def questionAnswer

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user_student = new User('name', "username", 1, User.Role.STUDENT)
        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)
        userRepository.save(user_student)

        user_teacher = new User('name-teacher', "username-teacher", 2, User.Role.TEACHER)
        user_teacher.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_teacher)
        userRepository.save(user_teacher)

        question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        option = new Option()
        option.setSequence(SEQUENCE)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        optionRepository.save(option)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question, SEQUENCE)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(user_student, quiz)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)
    }

    def "clarification requests exist and student gets"() {
        given: "two clarification requests"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest1)
        clarificationRequestRepository.save(clarificationRequest2)

        when:
        def result = questionDiscussionService.getClarificationRequests(user_student.getUsername(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 2
        def clarificationRequestDto = result.get(0)
        clarificationRequestDto.getId() != null
        clarificationRequestDto.getContent() == CLARIFICATION_CONTENT
        clarificationRequestDto.getStatus() == ClarificationRequest.Status.OPEN.name()
        clarificationRequestDto.getQuestionAnswerDto().getQuestion().getId() == question.getId()
        clarificationRequestDto.getUsername() == user_student.getUsername()
    }

    def "clarification requests exist and teacher gets"() {
        given: "two clarification requests"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest1)
        clarificationRequestRepository.save(clarificationRequest2)

        when:
        def result = questionDiscussionService.getClarificationRequests(user_teacher.getUsername(), courseExecution.getId())

        then: "the returned data is correct"
        result.size() == 2
        def clarificationRequestDto = result.get(0)
        clarificationRequestDto.getId() != null
        clarificationRequestDto.getContent() == CLARIFICATION_CONTENT
        clarificationRequestDto.getStatus() == ClarificationRequest.Status.OPEN.name()
        clarificationRequestDto.getQuestionAnswerDto().getQuestion().getId() == question.getId()
        clarificationRequestDto.getUsername() == user_student.getUsername()
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }
}
