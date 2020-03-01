package pt.ulisboa.tecnico.socialsoftware.tutor.questionSugest.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import spock.lang.Specification

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

    def setup(){

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user1 = new User('name', "username", 1, User.Role.STUDENT)
        user1.getCourseExecutions().add(courseExecution)
        user2 = new User('name', "username", 2, User.Role.STUDENT)
        user2.getCourseExecutions().add(courseExecution)
        user3 = new User('name', "username", 3, User.Role.TEACHER)
        user3.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user1)
        courseExecution.getUsers().add(user2)

    }

    def "create a question suggestion with no image and one option"(){
        // create suggestion with one option and no image
        expect: false
    }

    def "create a question with image and two options"(){
        // create suggestion with two option and an image
        expect: false
    }

    def "create two suggestion for the same student"(){
        // create two suggestion
        expect: false
    }

    def "create two suggestion for different student"(){
        // create two suggestion, related to different students
        expect: false
    }

    def "given student doesnt exist"(){
        // an exception is thrown
        expect: false
    }

}

