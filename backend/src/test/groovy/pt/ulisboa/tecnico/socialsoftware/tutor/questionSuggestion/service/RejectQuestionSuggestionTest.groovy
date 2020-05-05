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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.JustificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NULL_ARGUMENTS_JUTIFICATIONDTO
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NULL_ARGUMENTS_SUGGESTIONID
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NULL_ARGUMENTS_USERID
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.JUSTIFICATION_MISSING_DATA
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_ACCEPTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_REJECTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_STUDENT

@DataJpaTest
class RejectQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String QUESTION_TITLE = "question_title"
    public static final String QUESTION_CONTENT = "question_content"
    public static final String JUSTIFICATION_CONTENT = "justification_content"
    public static final String IMAGE_URL = "image_url"
    public static final String OPTION_CONTENT = "option content"

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
    JustificationRepository justificationRepository

    @Autowired
    UserRepository userRepository

    def course = new Course()
    def question = new Question()
    def questionSuggestion = new QuestionSuggestion()

    def setup() {
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
        question.setStatus(Question.Status.DISABLED)
        question.setCourse(course)

        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)
    }

    def "A pending question suggestion is rejected with justification by a teacher"() {
        given: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        and: "a user"
        def teacher = new User()
        teacher.setKey(2)
        teacher.setRole(User.Role.TEACHER)
        userRepository.save(teacher)

        when:
        questionSuggestionService.rejectQuestionSuggestion(teacher.getId(), questionSuggestion.getId(), justificationDto)

        then: "the suggestion becomes rejected"
        def result = questionSuggestionRepository.findAll().get(0)
        result != null
        result.getStatus() == QuestionSuggestion.Status.REJECTED

        and: "the underlying question status does not change"
        result.getQuestion().getStatus() == Question.Status.DISABLED

        and: "no new question is created"
        questionRepository.count() == 1L

        and: "a justification exists"
        result.getJustification() != null
        def justification = result.getJustification()
        justification.getId() != null
        justification.getContent() == JUSTIFICATION_CONTENT
        justification.getUser().getId() == teacher.getId()
        justification.getQuestionSuggestion().getId() == result.getId()
        justification.getImage() == null
    }

    def "A pending question suggestion is rejected with a justification with an image"() {
        given: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        and: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        and: "an image"
        def imageDto = new ImageDto()
        imageDto.setUrl(IMAGE_URL)
        imageDto.setWidth(20)
        justificationDto.setImage(imageDto)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestion.getId(), justificationDto)

        then: "the suggestion becomes rejected"
        def result = questionSuggestionRepository.findAll().get(0)
        result != null
        result.getStatus() == QuestionSuggestion.Status.REJECTED

        and: "the underlying question status does not change"
        result.getQuestion().getStatus() == Question.Status.DISABLED

        and: "a justification exists"
        result.getJustification() != null
        def justification = result.getJustification()
        justification.getId() != null
        justification.getContent() == JUSTIFICATION_CONTENT
        justification.getUser().getId() == user.getId()
        justification.getQuestionSuggestion().getId() == result.getId()

        and: "with an image"
        justification.getImage() != null
        def image = justification.getImage()
        image.getId() != null
        image.getWidth() == 20
        image.getUrl() == IMAGE_URL
        image.getJustification().getId() == justification.getId()
        image.getQuestion() == null
    }

    @Unroll
    def "A #status question suggestion is rejected"() {
        given: "a question suggestion"
        def suggestion = new QuestionSuggestion()
        suggestion.setQuestion(question)
        suggestion.setStatus(status)
        questionSuggestionRepository.save(suggestion)

        and: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        and: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), suggestion.getId(), justificationDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == errorMessage

        where:
        status                             || errorMessage
        QuestionSuggestion.Status.ACCEPTED || QUESTION_SUGGESTION_ALREADY_ACCEPTED
        QuestionSuggestion.Status.REJECTED || QUESTION_SUGGESTION_ALREADY_REJECTED
    }

    def "A question suggestion is rejected with a justification without content"() {
        given: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        and: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(null)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestion.getId(), justificationDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == JUSTIFICATION_MISSING_DATA
    }

    def "A question suggestion is rejected by a student user"() {
        given: "a user"
        def student = new User()
        student.setKey(2)
        student.setRole(User.Role.STUDENT)
        userRepository.save(student)

        and: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        when:
        questionSuggestionService.rejectQuestionSuggestion(student.getId(), questionSuggestion.getId(), justificationDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_IS_STUDENT
    }

    def "A question suggestion is rejected with a justification and no user"() {
        given: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        when:
        questionSuggestionService.rejectQuestionSuggestion(null, questionSuggestion.getId(), justificationDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_USERID
    }

    def "The question suggestion to rejected does not exist"() {
        given: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        and: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), null, justificationDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_SUGGESTIONID
    }

    def "A question suggestion is rejected without justification"() {
        given: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestion.getId(), null)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_JUTIFICATIONDTO
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
