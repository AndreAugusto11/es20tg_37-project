package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class GetQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String STUDENT_NAME = "Student Name"
    public static final String STUDENT_USERNAME = "Student Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    def course = new Course()
    def student = new User()
    def question = new Question()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User()
        student.setKey(1)
        student.setName(STUDENT_NAME)
        student.setUsername(STUDENT_USERNAME)
        student.setRole(User.Role.STUDENT)
        userRepository.save(student)

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

    def "A question suggestion is retrieved"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        def result = questionSuggestionService.getQuestionSuggestions(student.getId(), course.getId())

        then:
        result.size() == 1
        def resQuestionSuggestion = result.get(0)
        resQuestionSuggestion != null
        resQuestionSuggestion.getId() != null
        resQuestionSuggestion.getStatus() == QuestionSuggestion.Status.PENDING.name()
        resQuestionSuggestion.getQuestionDto() != null
        resQuestionSuggestion.getQuestionDto().getId() != null
        resQuestionSuggestion.getTitle() == QUESTION_TITLE
        resQuestionSuggestion.getContent() == QUESTION_CONTENT
        resQuestionSuggestion.getQuestionDto().getType() == Question.Type.SUGGESTION.name()
        resQuestionSuggestion.getQuestionDto().getStatus() == Question.Status.DISABLED.name()
    }

    def "Cannot retrieve a question suggestion with invalid user id"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.getQuestionSuggestions(0, course.getId())

        then: "An exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "Cannot retrieve a question suggestion with invalid course id"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.getQuestionSuggestions(student.getId(), 0)

        then: "An exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == COURSE_NOT_FOUND
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
