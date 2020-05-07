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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.CLARIFICATION_REQUEST_NOT_DEFINED

@DataJpaTest
class GetClarificationRequestAnswersServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "clarification request content"
    public static final String CLARIFICATION_ANSWER_CONTENT = "clarification answer content"
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

    @Autowired
    ClarificationRequestAnswerRepository clarificationRequestAnswerRepository

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
    def clarificationRequest

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

        clarificationRequest = new ClarificationRequest(questionAnswer, question, user_student, CLARIFICATION_CONTENT)
        questionAnswer.addClarificationRequest(clarificationRequest)
        question.addClarificationRequest(clarificationRequest)
        user_student.addClarificationRequest(clarificationRequest)
        clarificationRequestRepository.save(clarificationRequest)
    }

    def "get two answers from clarification request"() {
        given: "two clarification request answers"
        def clarificationRequestAnswer1 = new ClarificationRequestAnswer(clarificationRequest, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        def clarificationRequestAnswer2 = new ClarificationRequestAnswer(clarificationRequest, ClarificationRequestAnswer.Type.STUDENT_ANSWER, user_student, CLARIFICATION_ANSWER_CONTENT)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer1)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer2)

        when:
        def result = questionDiscussionService.getClarificationRequestAnswers(clarificationRequest.getId())

        then: "the returned data has the correct size"
        result.size() == 2
        and: "there is the teacher's answer"
        def clarificationRequestAnswerDto = result.get(0)
        clarificationRequestAnswerDto.getId() != null
        clarificationRequestAnswerDto.getContent() == CLARIFICATION_ANSWER_CONTENT
        clarificationRequestAnswerDto.getType() == ClarificationRequestAnswer.Type.TEACHER_ANSWER
        clarificationRequestAnswerDto.getName() == user_teacher.getName()
        clarificationRequestAnswerDto.getUsername() == user_teacher.getUsername()
        and: "there is the student's answer"
        def clarificationRequestAnswerDtoStudent = result.get(1)
        clarificationRequestAnswerDtoStudent.getId() != null
        clarificationRequestAnswerDtoStudent.getContent() == CLARIFICATION_ANSWER_CONTENT
        clarificationRequestAnswerDtoStudent.getType() == ClarificationRequestAnswer.Type.STUDENT_ANSWER
        clarificationRequestAnswerDtoStudent.getName() == user_student.getName()
        clarificationRequestAnswerDtoStudent.getUsername() == user_student.getUsername()
    }

    def "invalid argument: clarification_request_id=null"() {
        given: "a clarification request answer"
        def clarificationRequestAnswer = new ClarificationRequestAnswer(clarificationRequest, ClarificationRequestAnswer.Type.TEACHER_ANSWER, user_teacher, CLARIFICATION_ANSWER_CONTENT)
        clarificationRequestAnswerRepository.save(clarificationRequestAnswer)

        when:
        questionDiscussionService.getClarificationRequestAnswers(null)

        then: "exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == CLARIFICATION_REQUEST_NOT_DEFINED
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }
}
