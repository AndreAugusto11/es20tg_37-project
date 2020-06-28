package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service.performanceTest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.PublicClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreatePublicClarificationRequestServiceSpockPerformanceTest extends Specification {
    public static final Integer NUMBER_OF_ITERATIONS = 1
    public static final String USERNAME_TEACHER = "username_teacher"
    public static final String USERNAME_STUDENT = "username_student"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
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

    @Autowired
    PublicClarificationRequestRepository publicClarificationRequestRepository

    def user_teacher
    def user_student
    def course
    def courseExecution
    def question
    def option
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

    def "performance testing to create 1000 public clarification request"() {
        given: "a clarification request"
        def clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)

        and: "a clarification request dto"
        def clarificationRequestDto = new ClarificationRequestDto(clarificationRequest)

        and: "10000 clarification requests"
        1.upto(NUMBER_OF_ITERATIONS, {
            questionDiscussionService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)
        })
        List<ClarificationRequest> clarificationRequestList = clarificationRequestRepository.findAll()

        when: "10000 public clarification requests are created"
        1.upto(NUMBER_OF_ITERATIONS, {
            questionDiscussionService.createPublicClarificationRequest(clarificationRequestList.pop().getId())
        })

        then: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
