package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service.performanceTest

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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.JustificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class UpdateRejectedQuestionSuggestionTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String URL = "URL"
    public static final String JUSTIFICATION_CONTENT = "justification_content"
    public static final String QUESTIONSUGGESTION_TITLE1 = "question suggestion title 1"
    public static final String QUESTIONSUGGESTION_TITLE2 = "question suggestion title 2"
    public static final String QUESTIONSUGGESTION_CONTENT1 = "question suggestion content 1"
    public static final String QUESTIONSUGGESTION_CONTENT2 = "question suggestion content 2"
    public static final Integer QUESTIONSUGGESITONID = 1
    public static final String OPTION_CONTENT1 = "option content 1"
    public static final String OPTION_CONTENT2 = "option content 2"

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

    def course
    def courseExecution
    def user
    def questionDto1
    def questionDto2
    def justificationDto
    def optionDto1
    def optionDto2
    def options1
    def options2
    def questionSuggestionDto1
    def questionSuggestionDto1R
    def questionSuggestionDto2
    def questionSuggestion
    def questionSuggestionR

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User("name1", "username1", QUESTIONSUGGESITONID, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        user.setEnrolledCoursesAcronyms(ACRONYM)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        justificationDto = new JustificationDto()
        justificationDto.setKey(2)
        justificationDto.setContent(JUSTIFICATION_CONTENT)

        optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_CONTENT1)
        optionDto1.setCorrect(true)
        options1 = new ArrayList<OptionDto>()
        options1.add(optionDto1)


        optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options2 = new ArrayList<OptionDto>()
        options2.add(optionDto2)

        questionDto1 = new QuestionDto()
        questionDto1.setTitle(QUESTIONSUGGESTION_TITLE1)
        questionDto1.setContent(QUESTIONSUGGESTION_CONTENT1)
        questionDto1.setKey(1)
        questionDto1.setStatus(Question.Status.PENDING.name())
        questionDto1.setCreationDate("2020-04-16 17:51")
        questionDto1.setOptions(options1)

        questionDto2 = new QuestionDto()
        questionDto2.setTitle(QUESTIONSUGGESTION_TITLE2)
        questionDto2.setContent(QUESTIONSUGGESTION_CONTENT2)
        questionDto2.setKey(2)
        questionDto2.setStatus(Question.Status.PENDING.name())
        questionDto2.setCreationDate("2020-04-16 17:51")
        questionDto2.setOptions(options2)

        questionSuggestionDto1 = new QuestionSuggestionDto()
        questionSuggestionDto1.setQuestionDto(questionDto1)
        questionSuggestionDto1.setId(1)
        questionSuggestionDto1.setStatus(QuestionSuggestion.Status.REJECTED.name())
        questionSuggestionDto1.setJustificationDto(justificationDto)

        questionSuggestionDto1R = new QuestionSuggestionDto()
        questionSuggestionDto1R.setQuestionDto(questionDto1)
        questionSuggestionDto1R.setId(1)
        questionSuggestionDto1R.setStatus(QuestionSuggestion.Status.PENDING.name())
        questionSuggestionDto1R.setJustificationDto(justificationDto)

        questionSuggestionDto2 = new QuestionSuggestionDto()
        questionSuggestionDto2.setQuestionDto(questionDto2)
        questionSuggestionDto2.setId(2)
        questionSuggestionDto2.setStatus(QuestionSuggestion.Status.PENDING.name())
        questionSuggestionDto2.setJustificationDto(justificationDto)

        questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto1)
        questionSuggestionR = new QuestionSuggestion(user, course, questionSuggestionDto1R)
        questionSuggestionRepository.save(questionSuggestion)
        questionSuggestionRepository.save(questionSuggestionR)

    }

    def "update rejected question suggestion"() {
        given: "nothing"


        when: "a suggestion is updated"
        questionSuggestionService.updateRejectedQuestionSuggestion(questionSuggestion.getId(), questionSuggestionDto2)

        then: "the suggestion must be updated with the title, content and options of the questionSuggestionDto given"
        def result = questionSuggestionRepository.findAll().get(0)
        result.getId() == questionSuggestion.getId()
        result.getStatus() == QuestionSuggestion.Status.PENDING
        result.getTitle() == QUESTIONSUGGESTION_TITLE2
        result.getContent() == QUESTIONSUGGESTION_CONTENT2
        result.getUser().getName() == "name1"
        result.getImage() == null
        result.getOptions().size() == 2
        result.getUser().getQuestionsSuggestion().contains(result)
        def resOption = result.getOptions().get(1)
        resOption.getContent() == OPTION_CONTENT2
        resOption.getCorrect()
    }

    def "update a question suggestion that is not rejected"() {
        given: "nothing"

        when: "a suggestion is updated"
        questionSuggestionService.updateRejectedQuestionSuggestion(questionSuggestionR.getId(), questionSuggestionDto2)

        then: "an exception is thwron"
        TutorException exception = thrown()
        exception.getErrorMessage() == QUESTION_SUGGESTION_NOT_REJECTED
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
