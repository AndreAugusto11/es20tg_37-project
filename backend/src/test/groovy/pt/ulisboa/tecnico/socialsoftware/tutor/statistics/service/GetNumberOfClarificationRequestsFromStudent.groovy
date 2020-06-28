package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.PublicClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.PublicClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetNumberOfClarificationRequestsFromStudent extends Specification {
    public static final String USERNAME_STUDENT = "username_student"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0

    @Autowired
    StatsService statsService

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

        user_student = new User("name", USERNAME_STUDENT, 1, User.Role.STUDENT)
        user_student.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_student)
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
        quiz.setType(Quiz.QuizType.EXAM.name())
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

    def "Get the number of clarification requests created by the student"() {
        given: "two clarification requests"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest1)
        clarificationRequestRepository.save(clarificationRequest2)

        when:
        def statsDto = statsService.getAllStats(user_student.getId(), courseExecution.getId())

        then: "statsDto should count two clarification requests"
        statsDto.getTotalClarificationRequests() == 2;
    }

    def "Get the number of public clarification requests created by the student"() {
        given: "two clarification requests"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        clarificationRequestRepository.save(clarificationRequest1)
        clarificationRequestRepository.save(clarificationRequest2)
        and: "make them public"
        def publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest1)
        def publicClarificationRequest2 = new PublicClarificationRequest(course, clarificationRequest2)
        clarificationRequest2.setPublicClarificationRequest(publicClarificationRequest2)
        course.addPublicClarificationRequests(publicClarificationRequest2)
        publicClarificationRequestRepository.save(publicClarificationRequest1)
        publicClarificationRequestRepository.save(publicClarificationRequest2)

        when:
        def statsDto = statsService.getAllStats(user_student.getId(), courseExecution.getId())

        then: "statsDto should count two public clarification requests"
        statsDto.getTotalPublicClarificationRequests() == 2;
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
