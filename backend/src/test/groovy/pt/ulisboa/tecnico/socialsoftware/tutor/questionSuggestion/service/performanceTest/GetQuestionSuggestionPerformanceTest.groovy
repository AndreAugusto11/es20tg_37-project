package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service.performanceTest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetQuestionSuggestionPerformanceTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String USER_REAL_NAME = "name"
    public static final String USER_NAME = "username"
    public static final String QUESTION_TITLE = "title"
    public static final String QUESTION_CONTENT = "question content"
    public static final String OPTION_CONTENT = "option content"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution

    def user

    def options

    def questionDto
    def questionSuggestionDto

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(USER_REAL_NAME, USER_NAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        user.setEnrolledCoursesAcronyms(ACRONYM)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        options = new ArrayList<OptionDto>()
        def optionDto1 = new OptionDto()
        optionDto1.setContent(OPTION_CONTENT)
        optionDto1.setCorrect(true)
        options.add(optionDto1)
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_CONTENT)
        optionDto2.setCorrect(false)
        options.add(optionDto2)
    }

    def "performance testing to get 10000 question suggestions"() {
        given: "5 question suggestions"
        1.upto(5, {
            questionDto = new QuestionDto();
            questionDto.setTitle(QUESTION_TITLE + it)
            questionDto.setContent(QUESTION_CONTENT)
            questionDto.setKey(1 + it)
            questionDto.setType(Question.Type.SUGGESTION.name())
            questionDto.setStatus(Question.Status.DISABLED.name())
            questionDto.setOptions(options)
            questionDto.setCreationDate("2020-03-16 17:51")

            questionSuggestionDto = new QuestionSuggestionDto()
            questionSuggestionDto.setQuestionDto(questionDto)
            questionSuggestionDto.setStatus(QuestionSuggestion.Status.PENDING.name())

            def questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto)
            questionSuggestion.setCreationDate(LocalDateTime.now())

            questionSuggestionRepository.save(questionSuggestion)
        })

        when: "10000 suggestions are retrieved"
        1.upto(1, {
            questionSuggestionService.getQuestionSuggestions(user.getId(), course.getId())
        })

        then: true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {

    }
}
