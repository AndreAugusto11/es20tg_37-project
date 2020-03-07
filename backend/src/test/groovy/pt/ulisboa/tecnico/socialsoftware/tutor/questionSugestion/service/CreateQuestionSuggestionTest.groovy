package pt.ulisboa.tecnico.socialsoftware.tutor.questionSugest.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_USER

@DataJpaTest
class CreateQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String SUGGESTION_TITLE = 'suggestion title'
    public static final String SUGGESTION_CONTENT = 'suggestion content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = 'URL'

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def course
    def courseExecution
    def user1
    def user2
    def user3

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user1 = new User('name', "username", 1, User.Role.STUDENT)
        user1.getCourseExecutions().add(courseExecution)
        user1.setId(1)
        user2 = new User('name', "username", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        user1.setId(2)
        user3 = new User('name', "username", 3, User.Role.TEACHER)
        user3.getCourseExecutions().add(courseExecution)
        user1.setId(3)
        courseExecution.getUsers().add(user1)
        courseExecution.getUsers().add(user2)

    }

    def "create a question suggestion with no image and one option"() {
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionSuggestionDto.setOptions(options)

        when:
        questionSuggestionService.createSuggestionQuestion(user1.getId(), questionSuggestionDto)

        then: "the correct suggestion is inside the repository"

        def result = questionSuggestionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == QuestionSuggestion.Status.PENDING
        result.getTitle() == SUGGESTION_TITLE
        result.getContent() == SUGGESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        result.getCourse().getName() == COURSE_NAME
        course.getQuestions().contains(result)
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()
    }

    def "create a question with an image and two options"() {
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        and: 'an image'
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        questionSuggestionDto.setImage(image)

        and: 'two options'
        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionSuggestionDto.setOptions(options)

        when:
        questionSuggestionService.createSuggestionQuestion(user1.getId(), questionSuggestionDto)

        then: "the correct suggestion is inside the repository"
        questionSuggestionRepository.count() == 1L
        def result = questionSuggestionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == SUGGESTION_TITLE
        result.getContent() == SUGGESTION_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == URL
        result.getImage().getWidth() == 20
        result.getOptions().size() == 2
    }

    def "create two suggestions for different students"() {
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: 'are created two questions'
        questionSuggestionService.createSuggestionQuestion(user1.getId(), questionSuggestionDto)
        questionSuggestionDto.setKey(null)
        questionSuggestionService.createSuggestionQuestion(user2.getId(), questionSuggestionDto)

        then: "the two suggestions are created with the correct numbers"
        questionSuggestionRepository.count() == 2L
        def resultOne = questionSuggestionRepository.findAll().get(0)
        def resultTwo = questionSuggestionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
        user1.getNumberOfSuggestions() == 1;
        user2.getNumberOfSuggestions() == 1;
    }


    def "create two suggestions for the same students"() {
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: 'are created two questions'
        questionSuggestionService.createSuggestionQuestion(user1.getId(), questionSuggestionDto)
        questionSuggestionDto.setKey(null)
        questionSuggestionService.createSuggestionQuestion(user1.getId(), questionSuggestionDto)

        then: "the two suggestions are created with the correct numbers"
        questionSuggestionRepository.count() == 2L
        def resultOne = questionSuggestionRepository.findAll().get(0)
        def resultTwo = questionSuggestionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 3
        user1.getNumberOfSuggestions() == 2
    }

    def "given user that doesnt exist"() {
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: 'a suggestion with a none existing student'
        questionSuggestionService.createSuggestionQuestion(42, questionSuggestionDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "given a professor"(){
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: 'a suggestion with a none existing student'
        questionSuggestionService.createSuggestionQuestion(user3.getId(), questionSuggestionDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == INVALID_USER
    }

    def "given null id"(){
        given: "SuggestionQuestionDto"
        def questionSuggestionDto = new QuestionSuggestionDto()
        questionSuggestionDto.setKey(1)
        questionSuggestionDto.setTitle.(SUGGESTION_TITLE)
        questionSuggestionDto.setContent(SUGGESTION_CONTENT)
        questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

        when: 'a suggestion with a none existing student'
        questionSuggestionService.createSuggestionQuestion(null, questionSuggestionDto)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_NOT_FOUND
    }


    def "given null suggestion dto"(){
        given: "nothing"

        when: 'a suggestion with a none existing student'
        questionSuggestionService.createSuggestionQuestion(user1.getId(), null)

        then:
        TutorException exception = thrown()
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
