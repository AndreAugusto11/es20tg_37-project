package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto
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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class createClarificationRequestServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "clarification request content"
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

    @Shared
    def user
    def course
    def courseExecution
    @Shared
    def question
    def quizQuestion
    def option
    def quizAnswer
    def quiz
    def questionAnswer
    @Shared
    def questionAnswerId
    @Shared
    def username

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

        quizQuestion = new QuizQuestion(quiz, question, SEQUENCE)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setAnswerDate(ANSWER_DATE)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
    }

    def "create clarification request to answered question"() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)
        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(user.getName())
        clarificationRequestDto.setUsername(user.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswer(questionAnswerDto)
        // por o status???

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "the correct clarification request is inside the repository"
        def result = clarificationRequestRepository.findAll().get(0)
        result.id != null
        result.content == CLARIFICATION_CONTENT
        result.status == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswer
        result.getUser() == questionAnswer.getQuizAnswer().getUser()

    }

    def "create clarification request to non answered question"() {
        given: "a question answer not answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, null, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)
        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(user.getName())
        clarificationRequestDto.setUsername(user.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswer(questionAnswerDto)
        // por o status???

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "the correct clarification request is inside the repository"
        def result = clarificationRequestRepository.findAll().get(0)
        result.id != null
        result.content == CLARIFICATION_CONTENT
        result.status == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswer
        result.getUser() == questionAnswer.getQuizAnswer().getUser()
    }

    def "create clarification request to answered question, but user is diff"() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)
        and: "a user which didn't answer to this question"
        def diffUser = new User('name2', "username2", 2, User.Role.STUDENT)
        diffUser.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(diffUser)
        userRepository.save(diffUser)
        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(diffUser.getName())
        clarificationRequestDto.setUsername(diffUser.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswer(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_USER
    }

    /* def "create clarification request to answered question, but question is diff"() {
        given: "a question answered"
        questionAnswer = new QuestionAnswer()
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setTimeTaken(TIME_TAKEN)
        questionAnswer.setSequence(SEQUENCE)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)
        and: "a different question to the one answered"
        def diffQuestion = new Question()
        diffQuestion.setCourse(course)
        course.addQuestion(diffQuestion)
        diffQuestion.setKey(2)
        diffQuestion.setTitle(QUESTION_TITLE)
        diffQuestion.setContent(QUESTION_CONTENT)
        diffQuestion.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(diffQuestion)
        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(diffUser.getName())
        clarificationRequestDto.setUsername(diffUser.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswer(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), diffQuestion.getId(), questionAnswer.getQuizAnswer().getUser().getId(), CLARIFICATION_CONTENT)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_QUESTION
    } */

    @Unroll
    def "invalid arguments: questionAnswerId=#questionAnswerIdInput | username=#usernameInput | content=#content || errorMessage=#errorMessage "() {
        given: "a question answer"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)
        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(content)
        clarificationRequestDto.setName(user.getName())
        clarificationRequestDto.setUsername(usernameInput)
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswer(questionAnswerDto)
        questionAnswerId = questionAnswer.getId()
        username = user.getUsername()

        when:
        questionDiscussionService.createClarificationRequest(questionAnswerIdInput, clarificationRequestDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        questionAnswerIdInput  | usernameInput | content                || errorMessage
        null                   | username      | CLARIFICATION_CONTENT  || QUESTION_ANSWER_NOT_FOUND
        questionAnswerId       | null          | CLARIFICATION_CONTENT  || COURSE_EXECUTION_ACRONYM_IS_EMPTY
        questionAnswerId       | username      | null                   || COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
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
