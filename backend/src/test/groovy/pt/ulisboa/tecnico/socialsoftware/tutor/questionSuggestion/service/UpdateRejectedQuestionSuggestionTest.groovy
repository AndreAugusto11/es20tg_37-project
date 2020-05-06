package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateRejectedQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"

    public static final String QUESTION_TITLE = "title"
    public static final String QUESTION_CONTENT = "Is Math Related to Science?"
    public static final String OPTION_CONTENT = "option content"
    public static final String JUSTIFICATION_CONTENT = "justification_content"

    public static final String QUESTION_NEW_TITLE = "title"
    public static final String QUESTION_NEW_CONTENT = "Is Math Related to Science?"
    public static final String OPTION_NEW_CONTENT = "option content"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    def course = new Course()

    def rejOption = new Option()
    def rejQuestion = new Question()
    def justification = new Justification()
    def rejQuestionSuggestion = new QuestionSuggestion()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        rejOption.setContent(OPTION_CONTENT)
        rejOption.setCorrect(true)
        rejOption.setSequence(0)

        rejQuestion.setKey(1)
        rejQuestion.setTitle(QUESTION_TITLE)
        rejQuestion.setContent(QUESTION_CONTENT)
        rejQuestion.addOption(rejOption)
        rejQuestion.setType(Question.Type.SUGGESTION)
        rejQuestion.setCourse(course)

        justification.setKey(1)
        justification.setContent(JUSTIFICATION_CONTENT)

        rejQuestionSuggestion.setQuestion(rejQuestion)
        rejQuestionSuggestion.setStatus(QuestionSuggestion.Status.REJECTED)
        rejQuestionSuggestion.setJustification(justification)
        questionSuggestionRepository.save(rejQuestionSuggestion)
    }

    def "update rejected question suggestion"() {
        given: "an optionDto"
        def options = new ArrayList()
        def optionDto = new OptionDto()
        optionDto.setId(rejOption.getId())
        optionDto.setContent(OPTION_NEW_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        options.add(optionDto)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_NEW_TITLE)
        questionDto.setContent(QUESTION_NEW_CONTENT)
        questionDto.setOptions(options)
        questionDto.setType(Question.Type.SUGGESTION.name())

        and: "a suggestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setQuestionDto(questionDto)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: "a suggestion is updated"
        questionSuggestionService.updateRejectedQuestionSuggestion(rejQuestionSuggestion.getId(), questionSuggestionDto)

        then: "the suggestion must be updated with the values of the given questionSuggestionDto"
        def result = questionSuggestionRepository.findAll().get(0)
        result.getId() == rejQuestionSuggestion.getId()
        result.getStatus() == QuestionSuggestion.Status.PENDING
        result.getTitle() == QUESTION_NEW_TITLE
        result.getContent() == QUESTION_NEW_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_NEW_CONTENT
        resOption.getCorrect()
    }

    def "update a question suggestion that is not rejected"() {
        given: "an option from a suggestion"
        def option = new Option()
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)

        and: "the underlying question from a suggestion"
        def question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.addOption(option)
        question.setType(Question.Type.SUGGESTION)
        question.setCourse(course)

        and: "a pending suggestion"
        def questionSuggestion = new QuestionSuggestion()
        questionSuggestion.setQuestion(question)
        questionSuggestion.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion)

        and: "an optionDto"
        def options = new ArrayList()
        def optionDto = new OptionDto()
        optionDto.setId(option.getId())
        optionDto.setContent(OPTION_NEW_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        options.add(optionDto)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_NEW_TITLE)
        questionDto.setContent(QUESTION_NEW_CONTENT)
        questionDto.setOptions(options)
        questionDto.setType(Question.Type.SUGGESTION.name())

        and: "a suggestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setQuestionDto(questionDto)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: "a suggestion is updated"
        questionSuggestionService.updateRejectedQuestionSuggestion(questionSuggestion.getId(), questionSuggestionDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_SUGGESTION_NOT_REJECTED
    }

    def "update question suggestion given no suggestion id"() {
        given: "an optionDto"
        def options = new ArrayList()
        def optionDto = new OptionDto()
        optionDto.setId(rejOption.getId())
        optionDto.setContent(OPTION_NEW_CONTENT)
        optionDto.setCorrect(true)
        optionDto.setSequence(0)
        options.add(optionDto)

        and: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_NEW_TITLE)
        questionDto.setContent(QUESTION_NEW_CONTENT)
        questionDto.setOptions(options)
        questionDto.setType(Question.Type.SUGGESTION.name())

        and: "a suggestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setQuestionDto(questionDto)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when:
        questionSuggestionService.updateRejectedQuestionSuggestion(null, questionSuggestionDto)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_SUGGESTIONID
    }

    def "update question suggestion given no suggestion dto"() {
        given: "no suggestion dto"

        when:
        questionSuggestionService.updateRejectedQuestionSuggestion(rejQuestionSuggestion.getId(), null)

        then: "an exception is thrown"
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_NULL_ARGUMENTS_SUGGESTION
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
