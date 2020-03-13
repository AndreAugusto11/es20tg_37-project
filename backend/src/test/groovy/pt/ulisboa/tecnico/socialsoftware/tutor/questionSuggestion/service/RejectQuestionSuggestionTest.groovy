package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.JustificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NULL_ARGUMENTS
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.JUSTIFICATION_MISSING_DATA
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_ACCEPTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_SUGGESTION_ALREADY_REJECTED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_STUDENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_IS_TEACHER
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class RejectQuestionSuggestionTest extends Specification {

    public static final String QUESTION_TITLE = "question_title"
    public static final String QUESTION_CONTENT = "question_content"
    public static final String JUSTIFICATION_CONTENT = "justification_content"
    public static final String IMAGE_URL = "image_url"

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

    def question
    def questionSuggestion

    def setup() {
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.DISABLED)
        questionRepository.save(question)

        questionSuggestion = new QuestionSuggestion()
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
        questionSuggestionRepository.count() == 1L
        def result = questionSuggestionRepository.findAll().get(0)
        result.getStatus() == QuestionSuggestion.Status.REJECTED
        and: "the question status does not change"
        result.getQuestion().getStatus() == Question.Status.DISABLED
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
        and: 'an image'
        def imageDto = new ImageDto()
        imageDto.setUrl(IMAGE_URL)
        imageDto.setWidth(20)
        justificationDto.setImage(imageDto)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestion.getId(), justificationDto)

        then: "the suggestion becomes rejected"
        questionSuggestionRepository.count() == 1L
        def result = questionSuggestionRepository.findAll().get(0)
        result.getStatus() == QuestionSuggestion.Status.REJECTED
        and: "the question status does not change"
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

    def "An accepted question suggestion is rejected"() {
        given: "a question suggestion"
        def suggestion = new QuestionSuggestion()
        suggestion.setQuestion(question)
        suggestion.setStatus(QuestionSuggestion.Status.ACCEPTED)
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
        exception.getErrorMessage() == QUESTION_SUGGESTION_ALREADY_ACCEPTED

    }

    def "A rejected question suggestion is rejected"() {
        given: "a question suggestion"
        def suggestion = new QuestionSuggestion()
        suggestion.setQuestion(question)
        suggestion.setStatus(QuestionSuggestion.Status.REJECTED)
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
        exception.getErrorMessage() == QUESTION_SUGGESTION_ALREADY_REJECTED
    }

    def "A question suggestion is rejected without justification"() {
        given: "a user"
        def user = new User()
        user.setKey(2)
        user.setRole(User.Role.TEACHER)
        userRepository.save(user)

        when:
        questionSuggestionService.rejectQuestionSuggestion(user.getId(), questionSuggestion.getId(), null)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS
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

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == JUSTIFICATION_MISSING_DATA
    }

    def "A question suggestion is rejected with a justification and no user"() {
        given: "a justification"
        def justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        when:
        questionSuggestionService.rejectQuestionSuggestion(null, questionSuggestion.getId(), justificationDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_NOT_FOUND
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

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_IS_STUDENT
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
