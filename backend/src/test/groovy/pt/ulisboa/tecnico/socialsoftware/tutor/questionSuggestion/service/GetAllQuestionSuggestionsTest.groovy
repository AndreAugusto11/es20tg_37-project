package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
class GetAllQuestionSuggestionsTest extends Specification {

    public static final String COURSE1_NAME = "Software Architecture"
    public static final String COURSE2_NAME = "Distributed Systems"
    public static final String STUDENT1_NAME = "Student1 Name"
    public static final String STUDENT2_NAME = "Student2 Name"
    public static final String STUDENT1_USERNAME = "Student1 Username"
    public static final String STUDENT2_USERNAME = "Student2 Username"
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

    def course1 = new Course()
    def course2 = new Course()

    def student1 = new User()
    def student2 = new User()

    def question1 = new Question()
    def question2 = new Question()

    def setup() {
        course1.setName(COURSE1_NAME)
        course1.setType(Course.Type.TECNICO)
        courseRepository.save(course1)

        course2.setName(COURSE2_NAME)
        course2.setType(Course.Type.TECNICO)
        courseRepository.save(course2)

        student1 = new User()
        student1.setKey(1)
        student1.setName(STUDENT1_NAME)
        student1.setUsername(STUDENT1_USERNAME)
        student1.setRole(User.Role.STUDENT)
        userRepository.save(student1)

        student2 = new User()
        student2.setKey(2)
        student2.setName(STUDENT2_NAME)
        student2.setUsername(STUDENT2_USERNAME)
        student2.setRole(User.Role.STUDENT)
        userRepository.save(student2)

        def option1 = new Option()
        option1.setContent(OPTION_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(0)

        question1.setKey(1)
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.addOption(option1)
        question1.setType(Question.Type.SUGGESTION)
        question1.setCourse(course1)

        def option2 = new Option()
        option2.setContent(OPTION_CONTENT)
        option2.setCorrect(true)
        option2.setSequence(0)

        question2.setKey(2)
        question2.setTitle(QUESTION_TITLE)
        question2.setContent(QUESTION_CONTENT)
        question2.addOption(option2)
        question2.setType(Question.Type.SUGGESTION)
        question2.setCourse(course2)
    }

    def "Create two question suggestions for different courses and retrieve both"() {
        given: "Two question suggestion"
        def questionSuggestion1 = new QuestionSuggestion()
        questionSuggestion1.setUser(student1)
        questionSuggestion1.setQuestion(question1)
        questionSuggestion1.setCreationDate(LocalDateTime.now())
        questionSuggestion1.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion1)

        def questionSuggestion2 = new QuestionSuggestion()
        questionSuggestion2.setUser(student2)
        questionSuggestion2.setQuestion(question2)
        questionSuggestion2.setCreationDate(LocalDateTime.now())
        questionSuggestion2.setStatus(QuestionSuggestion.Status.PENDING)
        questionSuggestionRepository.save(questionSuggestion2)

        when:
        def result = questionSuggestionService.getAllQuestionSuggestions()

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

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
