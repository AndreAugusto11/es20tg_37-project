package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class createClarificationRequestServiceSpockTest extends Specification {
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
    public static final String URL = 'URL'

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

        quizAnswer = new QuizAnswer(user, quiz)
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
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "the correct clarification request is inside the repository"
        clarificationRequestRepository.findAll().size() == 1
        def result = clarificationRequestRepository.findAll().get(0)
        result != null
        and: "has the correct values"
        result.getId() != null
        result.getContent() == CLARIFICATION_CONTENT
        result.getStatus() == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswer
        result.getQuestion() == questionAnswer.getQuizQuestion().getQuestion()
        result.getUser() == questionAnswer.getQuizAnswer().getUser()
        and: "is associated correctly"
        questionAnswer.getClarificationRequest().contains(result)
        question.getClarificationRequest().contains(result)
        user.getClarificationRequests().contains(result)

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
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "the correct clarification request is inside the repository"
        clarificationRequestRepository.findAll().size() == 1
        def result = clarificationRequestRepository.findAll().get(0)
        result != null
        and: "has the correct values"
        result.getId() != null
        result.getContent() == CLARIFICATION_CONTENT
        result.getStatus() == ClarificationRequest.Status.OPEN
        result.getQuestionAnswer() == questionAnswer
        result.getQuestion() == questionAnswer.getQuizQuestion().getQuestion()
        result.getUser() == questionAnswer.getQuizAnswer().getUser()
        and: "is associated correctly"
        questionAnswer.getClarificationRequest().contains(result)
        question.getClarificationRequest().contains(result)
        user.getClarificationRequests().contains(result)
    }

    def "create clarification request to answered question, but user didn't answer question"() {
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
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_USER
    }

    def "create clarification request to answered question, but user is a teacher"() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "a teacher"
        def diffUser = new User('name2', "username2", 2, User.Role.TEACHER)
        diffUser.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(diffUser)
        userRepository.save(diffUser)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(diffUser.getName())
        clarificationRequestDto.setUsername(diffUser.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_USER
    }

    def "create clarification request to answered question, but question is different to the one answered"() {
        given: "a question answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
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
        clarificationRequestDto.setName(user.getName())
        clarificationRequestDto.setUsername(user.getUsername())

        and: "a modified question in question answer dto"
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        questionAnswerDto.setQuestion(new QuestionDto(diffQuestion))
        questionAnswerDto.setOption(new OptionDto(option))
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_MISMATCH_QUESTION
    }

    def "create clarification request with question answer id null"() {
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
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(null, clarificationRequestDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == QUESTION_ANSWER_NOT_DEFINED

    }

    @Unroll
    def "invalid arguments: username=#username | content=#content || errorMessage=#errorMessage "() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(content)
        clarificationRequestDto.setName(user.getName())
        clarificationRequestDto.setUsername(username)
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        when:
        questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        username            | content                || errorMessage
        null                | CLARIFICATION_CONTENT  || USER_NOT_FOUND_USERNAME
        "     "             | CLARIFICATION_CONTENT  || USER_NOT_FOUND_USERNAME
        user.getUsername()  | null                   || CLARIFICATION_REQUEST_IS_EMPTY
        user.getUsername()  | "     "                || CLARIFICATION_REQUEST_IS_EMPTY
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}