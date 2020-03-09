package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_MISMATCH_USER

@DataJpaTest
class createClarificationRequestServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TIME_TAKEN = 1234
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String VERSION = 'B'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0
    public static final LocalDateTime ANSWER_DATE = LocalDateTime.now()

    @Autowired
    QuestionDiscussionService  questionDiscussionService

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

    def user
    def course
    def courseExecution
    def question
    def quizQuestion
    def optionOk
    def optionKO
    def option
    def quizAnswer
    def date
    def quiz
    def questionAnswer

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        option = new Option()
        option.setSequence(0)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        optionRepository.save(option)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        quiz.setCreationDate(LocalDateTime.now())
        quiz.setAvailableDate(LocalDateTime.now())
        quiz.setConclusionDate(LocalDateTime.now())
        quiz.setType(Quiz.QuizType.EXAM)
        quiz.setSeries(1)
        quiz.setVersion(VERSION)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(SEQUENCE)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(user)
        quizAnswer.setAnswerDate(ANSWER_DATE)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
    }

    def "create clarification request to answered question"() {
        // clarification request is created
        // status is open
        given: "a question answered"
        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setOption(option)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswerResult.getId(), questionAnswerResult.getQuizQuestion().getQuestion().getId(), questionAnswerResult.getQuizAnswer().getUser().getId(), CLARIFICATION_CONTENT)

        then: "the returned data are correct"
        def result = clarificationRequestRepository.findAll().get(0)
        result.id != null
        result.content == CLARIFICATION_CONTENT
        result.status == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswerResult
        result.getUser() == questionAnswerResult.getQuizAnswer().getUser()

    }

    def "create clarification request to non answered question"() {
        given: "a question answered"
        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswerResult.getId(), questionAnswerResult.getQuizQuestion().getQuestion().getId(), questionAnswerResult.getQuizAnswer().getUser().getId(), CLARIFICATION_CONTENT)

        then: "the returned data are correct"
        def result = clarificationRequestRepository.findAll().get(0)
        result.id != null
        result.content == CLARIFICATION_CONTENT
        result.status == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswerResult
        result.getUser() == questionAnswerResult.getQuizAnswer().getUser()
    }

    def "create clarification request to answered question, but user is diff"() {
        given: "a question answered"
        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        def questionAnswerResult = questionAnswerRepository.findAll().get(0)
        and: "a user which didn't answered"
        def user2 = new User('name2', "username2", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user2)
        userRepository.save(user2)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswerResult.getId(), questionAnswerResult.getQuizQuestion().getQuestion().getId(), user2.getId(), CLARIFICATION_CONTENT)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_USER
    }

    def "clarification request is empty"() {
        // an exception is thrown
        expect: false
    }

    def "clarification request is blank"() {
        // an exception is thrown
        expect: false
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }

}
