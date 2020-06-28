package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.JustificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class RemoveQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String STUDENT_NAME = "Student Name"
    public static final String STUDENT_USERNAME = "Student Username"
    public static final String TEACHER_NAME = "Teacher Name"
    public static final String TEACHER_USERNAME = "Teacher Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String JUSTIFICATION_CONTENT = "Justification Content"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    @Autowired
    JustificationRepository justificationRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository

    def course = new Course()
    def student = new User()
    def question = new Question()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User()
        student.setName(STUDENT_NAME)
        student.setUsername(STUDENT_USERNAME)
        student.setRole(User.Role.STUDENT)

        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)

        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.addOption(option)
        question.setType(Question.Type.SUGGESTION)
        question.setCourse(course)
    }

    def "An accepted question suggestion is removed"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.ACCEPTED)
        questionSuggestionRepository.save(questionSuggestion)

        when: "The suggestion is removed"
        questionSuggestionService.removeQuestionSuggestion(questionSuggestion.getId())

        then: "The suggestion is deleted"
        questionSuggestionRepository.count() == 0
        questionRepository.findByKey(1).empty
    }

    def "A rejected question suggestion is removed"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.REJECTED)

        and: "A user teacher"
        def teacher = new User()
        teacher.setName(TEACHER_NAME)
        teacher.setUsername(TEACHER_USERNAME)
        teacher.setRole(User.Role.TEACHER)

        and: "A justification"
        def justification = new Justification()
        justification.setKey(2)
        justification.setUser(teacher)
        justification.setContent(JUSTIFICATION_CONTENT)
        questionSuggestionRepository.save(questionSuggestion)

        when: "The suggestion is removed"
        questionSuggestionService.removeQuestionSuggestion(questionSuggestion.getId())

        then: "The suggestion is deleted"
        questionSuggestionRepository.count() == 0
        questionRepository.findByKey(1).empty
        justificationRepository.findByKey(2).empty
    }

    def "A pending question suggestion cannot be removed"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when: "The suggestion is removed"
        questionSuggestionService.removeQuestionSuggestion(questionSuggestion.getId())

        then: "An exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == CANNOT_DELETE_QUESTION_SUGGESTION
    }

    def "A question suggestion given an invalid id cannot be removed"() {
        when: "An invalid suggestion id is given"
        questionSuggestionService.removeQuestionSuggestion(0)

        then: "An exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_SUGGESTION_NOT_FOUND
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
