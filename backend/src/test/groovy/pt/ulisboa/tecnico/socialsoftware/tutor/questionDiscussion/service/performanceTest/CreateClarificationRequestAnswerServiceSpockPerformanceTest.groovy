package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service.performanceTest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
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

@DataJpaTest
class CreateClarificationRequestAnswerServiceSpockPerformanceTest extends Specification {
    public static final Integer NUMBER_OF_ITERATIONS = 1000
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

    def "performance testing to create 10000 clarification request answers"() {
        given: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setName(user_student.getName())
        clarificationRequestDto.setUsername(user_student.getUsername())
        def questionAnswerDto = new QuestionAnswerDto(questionAnswer)
        clarificationRequestDto.setQuestionAnswerDto(questionAnswerDto)

        and: "a clarification request answer dto"
        def clarificationRequestAnswerDto = new ClarificationRequestAnswerDto()
        clarificationRequestAnswerDto.setType(ClarificationRequestAnswer.Type.TEACHER_ANSWER)
        clarificationRequestAnswerDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestAnswerDto.setName(user_teacher.getName())
        clarificationRequestAnswerDto.setUsername(user_teacher.getUsername())

        and: "10000 clarification requests"
        1.upto(NUMBER_OF_ITERATIONS, {
            questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)
        })
        List<ClarificationRequest> clarificationRequestList = clarificationRequestRepository.findAll()

        when: "10000 clarification request answers are created"
        1.upto(NUMBER_OF_ITERATIONS, {
            questionDiscussionService.createClarificationRequestAnswer(clarificationRequestList.pop().getId(), clarificationRequestAnswerDto)
        })

        List<ClarificationRequestAnswer> clarificationRequestAnswersList = clarificationRequestAnswerRepository.findAll()

        then:
        clarificationRequestAnswersList.size() >= NUMBER_OF_ITERATIONS
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }
}
