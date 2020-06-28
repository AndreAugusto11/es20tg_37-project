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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UploadImageToQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final Course.Type COURSE_TYPE = Course.Type.TECNICO
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"

    public static final String IMAGE_TYPE_ONE = "png"
    public static final String IMAGE_TYPE_TWO = "jpeg"
    public static final String IMAGE_TYPE_ONE_URL = "SoftwareArchitectureTECNICOq1.png"
    public static final String IMAGE_TYPE_ONE_URL_ALT = "SoftwareArchitectureTECNICOq2.png"
    public static final String IMAGE_TYPE_TWO_URL = "SoftwareArchitectureTECNICOq1.jpeg"

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

    @Autowired
    ImageRepository imageRepository

    def course = new Course()
    def question = new Question()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(COURSE_TYPE)
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

    def "An image is uploaded to a suggestion without image"() {
        given: "a question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.uploadImageToQuestionSuggestion(questionSuggestion.getId(), IMAGE_TYPE_ONE)

        then: "the suggestion remains in the same state"
        def result = questionSuggestionRepository.findAll().get(0)
        result != null
        result.getStatus() == QuestionSuggestion.Status.PENDING

        and: "the underlying question is in the same state"
        questionRepository.count() == 1L
        def questionResult = questionRepository.findAll().get(0)
        questionResult.getId() != null
        questionResult.getKey() == 1
        questionResult.getStatus() == Question.Status.DISABLED
        questionResult.getType() == Question.Type.SUGGESTION
        questionResult.getTitle() == QUESTION_TITLE
        questionResult.getContent() == QUESTION_CONTENT
        questionResult.getOptions().size() == 1
        questionResult.getCourse().getName() == COURSE_NAME
        course.getQuestions().contains(questionResult)
        def resOption = questionResult.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()

        and: "an image is attached to the question"
        imageRepository.count() == 1L
        def imageResult = imageRepository.findAll().get(0)
        questionResult.getImage() == imageResult
        imageResult.getQuestion() == questionResult
        imageResult.getId() != null
        imageResult.getUrl() == IMAGE_TYPE_ONE_URL
    }

    def "An image is uploaded to a suggestion with an image"() {
        given: "an image"
        def image = new Image()
        image.setUrl("url")

        and: "a question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setQuestion(question)
        questionSuggestion.setImage(image)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.uploadImageToQuestionSuggestion(questionSuggestion.getId(), IMAGE_TYPE_TWO)

        then: "the image is altered"
        questionSuggestionRepository.count() == 1L
        def result = questionSuggestionRepository.findAll().get(0)
        imageRepository.count() == 1L
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_TYPE_TWO_URL
    }

    def "An image is uploaded to a suggestion whose question has no key"() {
        given: "an option"
        def newOption = new Option()
        newOption.setContent(OPTION_CONTENT)
        newOption.setCorrect(true)
        newOption.setSequence(0)

        and: "a question with no key"
        def noKeyQuestion = new Question()
        noKeyQuestion.setTitle(QUESTION_TITLE)
        noKeyQuestion.setContent(QUESTION_CONTENT)
        noKeyQuestion.addOption(newOption)
        noKeyQuestion.setType(Question.Type.SUGGESTION)
        noKeyQuestion.setCourse(course)

        and: "a question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setQuestion(noKeyQuestion)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.uploadImageToQuestionSuggestion(questionSuggestion.getId(), IMAGE_TYPE_ONE)

        then: "an image is attached to the question"
        questionSuggestionRepository.count() == 1L
        def result = questionSuggestionRepository.findAll().get(0)
        result.getQuestion().getKey() != null
        imageRepository.count() == 1L
        result.getImage().getId() != null
        result.getImage().getUrl() == IMAGE_TYPE_ONE_URL_ALT
    }

    def "Cannot upload an image to a suggestion given an invalid suggestion id"() {
        when:
        questionSuggestionService.uploadImageToQuestionSuggestion(0, IMAGE_TYPE_ONE)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_SUGGESTION_NOT_FOUND
    }

    def "Cannot upload an image to an empty suggestion"() {
        given: "an empty question suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        questionSuggestionService.uploadImageToQuestionSuggestion(questionSuggestion.getId(), IMAGE_TYPE_ONE)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == EMPTY_QUESTION_SUGGESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
