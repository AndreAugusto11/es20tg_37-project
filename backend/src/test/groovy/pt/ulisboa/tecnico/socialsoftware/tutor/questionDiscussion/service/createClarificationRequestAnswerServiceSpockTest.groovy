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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
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
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@DataJpaTest
class createClarificationRequestAnswerServiceSpockTest extends Specification {
    public static final String USERNAME = "username"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String EMPTY_CLARIFICATION_CONTENT = ""
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

    @Autowired
    ClarificationRequestAnswerRepository clarificationRequestAnswerRepository

    def user
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

        user = new User("name", USERNAME, 1, User.Role.TEACHER)
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

        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setOption(option)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
    }

    def "teacher creates clarification request answer to an opened clarification request"() {
        given: "an opened clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user.getName())
        clarificationRequestAnswerDto.setUsername(user.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "the clarification request is successfully created"
        def result = clarificationRequestAnswerRepository.findAll().get(0)
        result.getId() != null
        result.getType() == ClarificationRequestAnswer.Type.TEACHER_ANSWER
        result.content == CLARIFICATION_CONTENT
        result.getClarificationRequest().getStatus() == ClarificationRequest.Status.OPEN
    }


    def "teacher creates clarification request answer to a closed clarification request"() {
        given: "a closed clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        clarificationRequest.setStatus(ClarificationRequest.Status.CLOSED)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user.getName())
        clarificationRequestAnswerDto.setUsername(user.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_NO_LONGER_AVAILABLE
    }

    def "teacher from a different course execution creates clarification request answer"() {
        given: "an user not associated to the course execution"
        def userNotAssociated = new User('name1', "username1", 2, User.Role.TEACHER)
        userRepository.save(userNotAssociated)

        and: "an opened clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, userNotAssociated, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        userNotAssociated.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(userNotAssociated.getName())
        clarificationRequestAnswerDto.setUsername(userNotAssociated.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ACCESS_DENIED
    }

    def "student creates clarification request answer"() {
        given: "an opened clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.STUDENT_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user.getName())
        clarificationRequestAnswerDto.setUsername(user.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ACCESS_DENIED
    }


    @Unroll
    def "invalid arguments: type=#type | content=#content | username=#username || errorMessage=#errorMessage"() {
        given: "a clarification request"
        ClarificationRequest clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        ClarificationRequestAnswerDto clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setContent(content)
        clarificationRequestAnswerDto.setType(type)
        clarificationRequestAnswerDto.setName(user.getName())
        clarificationRequestAnswerDto.setUsername(username)

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        type                                           | content               | username || errorMessage
        null                                           | CLARIFICATION_CONTENT | USERNAME || CLARIFICATION_REQUEST_ANSWER_TYPE_NOT_DEFINED
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | null                  | USERNAME || CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | "   "                 | USERNAME || CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | CLARIFICATION_CONTENT | null     || USER_NOT_FOUND_USERNAME
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | CLARIFICATION_CONTENT | "    "   || USER_NOT_FOUND_USERNAME

        ClarificationRequestAnswer.Type.STUDENT_ANSWER | CLARIFICATION_CONTENT | USERNAME || ACCESS_DENIED
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }
    }
}