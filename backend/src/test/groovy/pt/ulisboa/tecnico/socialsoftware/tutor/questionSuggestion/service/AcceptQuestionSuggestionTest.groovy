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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_ACCEPTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_REJECTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_NOT_FOUND

@DataJpaTest
class AcceptQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository

    def course = new Course()
    def user = new User("name", "username", 323, User.Role.STUDENT)
    def question = new Question()

    def setup() {
        userRepository.save(user)

        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

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

    def "A pending question suggestion is accepted"() {
        given: "a question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setUser(user)
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.acceptQuestionSuggestion(questionSuggestion.getId())

        then: "the suggestion becomes accepted"
        def result = questionSuggestionRepository.findAll().get(0)
        result != null
        result.getStatus() == QuestionSuggestion.Status.ACCEPTED

        and: "a new question is created"
        questionRepository.count() == 2L
        def questionResult = questionRepository.findAll().get(1)
        questionResult.getId() != null
        questionResult.getKey() != 1
        questionResult.getStatus() == Question.Status.DISABLED
        questionResult.getType() == Question.Type.NORMAL
        questionResult.getTitle() == QUESTION_TITLE
        questionResult.getContent() == QUESTION_CONTENT
        questionResult.getImage() == null
        questionResult.getOptions().size() == 1
        questionResult.getCourse().getName() == COURSE_NAME
        course.getQuestions().contains(questionResult)
        def resOption = questionResult.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()
    }

    @Unroll
    def "Cannot accept a #status question suggestion"() {
        given: "a question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(status)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.acceptQuestionSuggestion(questionSuggestion.getId())

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == errorMessage

        where:
        status                             || errorMessage
        QuestionSuggestion.Status.ACCEPTED || QUESTION_SUGGESTION_ALREADY_ACCEPTED
        QuestionSuggestion.Status.REJECTED || QUESTION_SUGGESTION_ALREADY_REJECTED
    }

    def "Cannot accept a question suggestion given an invalid id"() {
        when:
        questionSuggestionService.acceptQuestionSuggestion(0)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_SUGGESTION_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
