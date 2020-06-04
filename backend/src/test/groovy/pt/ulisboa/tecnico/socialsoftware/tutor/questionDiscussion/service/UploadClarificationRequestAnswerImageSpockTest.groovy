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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequestAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Shared
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_ALREADY_HAS_IMAGE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_ANSWER_ALREADY_HAS_IMAGE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_ANSWER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_NOT_FOUND

@DataJpaTest
class UploadClarificationRequestAnswerImageSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "clarification request content"
    public static final String CLARIFICATION_ANSWER_CONTENT = "clarification request answer content"
    public static final Integer CLARIFICATION_REQUEST_ID_NOT_VALID = 1234
    public static final Integer CLARIFICATION_REQUEST_ANSWER_ID_NOT_VALID = 9876
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TIME_TAKEN = 1234
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0
    public static final String URL = 'URL'
    public static final String FILE_TYPE_PNG = 'PNG'

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

    @Autowired
    ImageRepository imageRepository

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

    def "upload image to created clarification request answer"() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "a Clarification Request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a Clarification Request Answer"
        def clarificationRequestAnswer = new ClarificationRequestAnswer(clarificationRequest, ClarificationRequestAnswer.Type.STUDENT_ANSWER, user, CLARIFICATION_ANSWER_CONTENT)

        def clarificationRequestAnswerId = clarificationRequestAnswerRepository.findAll().get(0).getId()

        when:
        questionDiscussionService.uploadClarificationRequestAnswerImage(clarificationRequestAnswerId, FILE_TYPE_PNG)

        then: "the clarification request answer is associated to the image inside the repository"
        clarificationRequestAnswerRepository.findAll().size() == 1
        def result = clarificationRequestAnswerRepository.findAll().get(0)
        result != null

        and: "has the correct values"
        result.getImage().getUrl() == course.getName().replaceAll("\\s", "") + course.getType() + "-CLAR_REQ-" + clarificationRequestAnswer.getClarificationRequest().getKey() + "-ANSW-" + clarificationRequestAnswer.getKey() + "." + FILE_TYPE_PNG;


        and: "the image is associated to the clarification request inside the repository"
        imageRepository.findAll().size() == 1
        def result2 = imageRepository.findAll().get(0)
        result2 != null
        result2.getClarificationRequestAnswer().getId() == clarificationRequestAnswerId
    }

    def "upload two images to the same clarification request answer"() {
        given: "a question answer answered"
        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer.addQuestionAnswer(questionAnswer)
        questionAnswerRepository.save(questionAnswer)

        and: "a Clarification Request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a Clarification Request Answer"
        def clarificationRequestAnswer = new ClarificationRequestAnswer(clarificationRequest, ClarificationRequestAnswer.Type.STUDENT_ANSWER, user, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswerId = clarificationRequestAnswerRepository.findAll().get(0).getId()

        when:
        questionDiscussionService.uploadClarificationRequestAnswerImage(clarificationRequestAnswerId, FILE_TYPE_PNG)
        questionDiscussionService.uploadClarificationRequestAnswerImage(clarificationRequestAnswerId, FILE_TYPE_PNG)

        then: "an exeption should be thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_ANSWER_ALREADY_HAS_IMAGE
    }

    def "upload an image to a non existing clarification request"() {
        when:
        questionDiscussionService.uploadClarificationRequestAnswerImage(CLARIFICATION_REQUEST_ANSWER_ID_NOT_VALID, FILE_TYPE_PNG)

        then: "an exeption should be thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_ANSWER_NOT_FOUND
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }
}
