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
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@DataJpaTest
class createClarificationRequestAnswerServiceSpockTest extends Specification {
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

    def "teacher creates clarification request answer to a non existing clarification request"() {
        given: "a null clarification request answer dto"
        def clarificationRequestDto = null

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user_teacher.getName())
        clarificationRequestAnswerDto.setUsername(user_teacher.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_NOT_DEFINED
    }


    def "teacher creates clarification request answer to an opened clarification request"() {
        given: "an opened clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user_teacher.getName())
        clarificationRequestAnswerDto.setUsername(user_teacher.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "the clarification request answer is inside the repository"
        clarificationRequestAnswerRepository.findAll().size() == 1
        def result = clarificationRequestAnswerRepository.findAll().get(0)
        result != null

        and: "has the correct values"
        result.getId() != null
        result.content == CLARIFICATION_CONTENT
        result.getUser() == user_teacher
        result.getClarificationRequest() == clarificationRequest
        result.getType() == ClarificationRequestAnswer.Type.TEACHER_ANSWER

        and: "is associated correctly"
        user_teacher.getClarificationRequestAnswers().contains(result)
        clarificationRequest.getClarificationRequestAnswer().contains(result)
        result.getClarificationRequest().getStatus() == ClarificationRequest.Status.OPEN
    }


    def "teacher creates clarification request answer to a closed clarification request"() {
        given: "a closed clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequest.setStatus(ClarificationRequest.Status.CLOSED)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user_teacher.getName())
        clarificationRequestAnswerDto.setUsername(user_teacher.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_NO_LONGER_AVAILABLE
    }


    def "teacher from a different course execution creates clarification request answer"() {
        given: "an user not associated to the course execution"
        def userNotAssociated = new User('name1', "username1", 3, User.Role.TEACHER)
        userRepository.save(userNotAssociated)

        and: "an opened clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
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
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setName(user_student.getName())
        clarificationRequestAnswerDto.setUsername(user_student.getUsername())

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ACCESS_DENIED
    }


    @Unroll
    def "invalid arguments: type=#type | content=#content | username=#username || errorMessage=#errorMessage"() {
        given: "a clarification request"
        ClarificationRequest clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        questionAnswer.setClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "a clarification request answer dto"
        ClarificationRequestAnswerDto clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setClarificationRequest(clarificationRequestDto)
        clarificationRequestAnswerDto.setContent(content)
        clarificationRequestAnswerDto.setType(type)
        clarificationRequestAnswerDto.setName(user_teacher.getName())
        clarificationRequestAnswerDto.setUsername(username)

        when:
        questionDiscussionService.createClarificationRequestAnswer(clarificationRequestAnswerDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        type                                           | content                     | username         || errorMessage
        null                                           | CLARIFICATION_CONTENT       | USERNAME_TEACHER || CLARIFICATION_REQUEST_ANSWER_TYPE_NOT_DEFINED
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | null                        | USERNAME_TEACHER || CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | "   "                       | USERNAME_TEACHER || CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | CLARIFICATION_CONTENT       | null             || USER_NOT_FOUND_USERNAME
        ClarificationRequestAnswer.Type.TEACHER_ANSWER | CLARIFICATION_CONTENT       | "    "           || USER_NOT_FOUND_USERNAME
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }
    }
}