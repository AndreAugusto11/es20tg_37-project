package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND

@DataJpaTest
class GetCourseQuestionSuggestionsTest extends Specification {

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
    def question1 = new Question()
    def question2 = new Question()

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

        def option1 = new Option()
        option1.setContent(OPTION_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(0)

        question1.setKey(1)
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.addOption(option1)
        question1.setType(Question.Type.SUGGESTION)
        question1.setCourse(course)

        def option2 = new Option()
        option2.setContent(OPTION_CONTENT)
        option2.setCorrect(true)
        option2.setSequence(0)

        question2.setKey(2)
        question2.setTitle(QUESTION_TITLE)
        question2.setContent(QUESTION_CONTENT)
        question2.addOption(option2)
        question2.setType(Question.Type.SUGGESTION)
        question2.setCourse(course)
    }

    def "Create two question suggestions and retrieve both"() {
        given: "Two question suggestion"
        def questionSuggestion1 = new QuestionSuggestion()
        questionSuggestion1.setUser(student)
        questionSuggestion1.setQuestion(question1)
        questionSuggestion1.setCreationDate(LocalDateTime.now())
        questionSuggestion1.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion1)

        def questionSuggestion2 = new QuestionSuggestion()
        questionSuggestion2.setUser(student)
        questionSuggestion2.setQuestion(question2)
        questionSuggestion2.setCreationDate(LocalDateTime.now())
        questionSuggestion2.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion2)

        when:
        def result = questionSuggestionService.getAllQuestionSuggestions(course.getId())

        then: "The first suggestion is retrieved intact"
        result.size() == 2

        def resQuestionSuggestion1 = result.get(0)
        resQuestionSuggestion1 != null
        resQuestionSuggestion1.getId() != null
        resQuestionSuggestion1.getStatus() == QuestionSuggestion.Status.PENDING.name()
        resQuestionSuggestion1.getQuestionDto() != null
        resQuestionSuggestion1.getQuestionDto().getId() != null
        resQuestionSuggestion1.getTitle() == QUESTION_TITLE
        resQuestionSuggestion1.getContent() == QUESTION_CONTENT
        resQuestionSuggestion1.getQuestionDto().getType() == Question.Type.SUGGESTION.name()
        resQuestionSuggestion1.getQuestionDto().getStatus() == Question.Status.DISABLED.name()

        and: "The second suggestion is retrieved intact"
        def resQuestionSuggestion2 = result.get(1)
        resQuestionSuggestion2 != null
        resQuestionSuggestion2.getId() != null
        resQuestionSuggestion2.getStatus() == QuestionSuggestion.Status.PENDING.name()
        resQuestionSuggestion2.getQuestionDto() != null
        resQuestionSuggestion2.getQuestionDto().getId() != null
        resQuestionSuggestion2.getTitle() == QUESTION_TITLE
        resQuestionSuggestion2.getContent() == QUESTION_CONTENT
        resQuestionSuggestion2.getQuestionDto().getType() == Question.Type.SUGGESTION.name()
        resQuestionSuggestion2.getQuestionDto().getStatus() == Question.Status.DISABLED.name()
    }

    def "Cannot retrieve question suggestions given an invalid course id"() {
        given: "A question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(student)
        questionSuggestion.setQuestion(question1)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.getAllQuestionSuggestions(0)

        then: "An exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == COURSE_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
