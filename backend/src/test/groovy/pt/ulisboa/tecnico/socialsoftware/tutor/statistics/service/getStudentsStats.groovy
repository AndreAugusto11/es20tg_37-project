package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.PublicClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import spock.lang.Specification

@DataJpaTest
class getStudentsStats extends Specification {
    public static final String USERNAME_STUDENT = "username_student"
    public static final String USERNAME_TEACHER = "username_teacher"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String CLARIFICATION_CONTENT = "Test"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0
    public static final String URL = "URL"
    public static final String QUESTIONSUGGESTION_TITLE1 = "question suggestion title 1"
    public static final String QUESTIONSUGGESTION_TITLE2 = "question suggestion title 2"
    public static final String QUESTIONSUGGESTION_CONTENT1 = "question suggestion content 1"
    public static final String QUESTIONSUGGESTION_CONTENT2 = "question suggestion content 2"
    public static final String OPTION_CONTENT1 = "option content 1"
    public static final String OPTION_CONTENT2 = "option content 2"

    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    PublicClarificationRequestRepository publicClarificationRequestRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    def course
    def courseExecution
    def user
    def user2
    def user_teacher
    def questionDto1
    def questionDto2
    def questionDto3
    def optionDto1
    def optionDto2
    def options1
    def options2
    def questionSuggestionDto1
    def questionSuggestionDto2
    def questionSuggestionDto3
    def questionSuggestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User("name1", "username1", 1, User.Role.STUDENT)
        user2 = new User("name2", "username2", 2, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        user2.getCourseExecutions().add(courseExecution)
        user.setEnrolledCoursesAcronyms(ACRONYM)
        user2.setEnrolledCoursesAcronyms(ACRONYM)
        courseExecution.getUsers().add(user)
        courseExecution.getUsers().add(user2)
        userRepository.save(user)
        userRepository.save(user2)

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
        questionDto1.setStatus(Question.Status.DISABLED.name())
        questionDto1.setCreationDate("2020-04-16 17:51")
        questionDto1.setOptions(options1)
        questionDto1.setType("SUGGESTION")

        questionDto2 = new QuestionDto()
        questionDto2.setTitle(QUESTIONSUGGESTION_TITLE2)
        questionDto2.setContent(QUESTIONSUGGESTION_CONTENT2)
        questionDto2.setKey(2)
        questionDto2.setStatus(Question.Status.DISABLED.name())
        questionDto2.setCreationDate("2020-04-16 17:51")
        questionDto2.setOptions(options2)
        questionDto2.setType("SUGGESTION")

        questionDto3 = new QuestionDto()
        questionDto3.setTitle(QUESTIONSUGGESTION_TITLE2)
        questionDto3.setContent(QUESTIONSUGGESTION_CONTENT2)
        questionDto3.setKey(3)
        questionDto3.setStatus(Question.Status.DISABLED.name())
        questionDto3.setCreationDate("2020-04-16 17:55")
        questionDto3.setOptions(options2)
        questionDto3.setType("SUGGESTION")

        questionSuggestionDto1 = new QuestionSuggestionDto()
        questionSuggestionDto1.setQuestionDto(questionDto1)
        questionSuggestionDto1.setId(1)
        questionSuggestionDto1.setStatus(QuestionSuggestion.Status.ACCEPTED.name())

        questionSuggestionDto2 = new QuestionSuggestionDto()
        questionSuggestionDto2.setQuestionDto(questionDto2)
        questionSuggestionDto2.setId(2)
        questionSuggestionDto2.setStatus(QuestionSuggestion.Status.PENDING.name())

        questionSuggestionDto3 = new QuestionSuggestionDto()
        questionSuggestionDto3.setQuestionDto(questionDto3)
        questionSuggestionDto3.setId(3)
        questionSuggestionDto3.setStatus(QuestionSuggestion.Status.PENDING.name())
    }

    def "Get a list with the students' stats"() {
        given: "two question suggestions"
        questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto1)
        questionSuggestionRepository.save(questionSuggestion)
        questionSuggestion = new QuestionSuggestion(user, course, questionSuggestionDto2)
        questionSuggestionRepository.save(questionSuggestion)

        and: "another question suggestion associated to another user"
        questionSuggestion = new QuestionSuggestion(user2, course, questionSuggestionDto3)
        questionSuggestionRepository.save(questionSuggestion)

        when:
        def result = statsService.getSimplifiedStudentsStats(courseExecution.getId())

        then: "the result should be the number of suggestion that the student made"
        result.size() == 2;
        if (result.get(0).getName() == user.getName()) {
            result.get(1).getName() == user2.getName()
        } else {
            result.get(0).getName() == user2.getName()
            result.get(1).getName() == user.getName()
        }

        if (result.get(0).getTotalNumberSuggestions() == 2) {
            result.get(1).getTotalNumberSuggestions() == 1
        } else {
            result.get(0).getTotalNumberSuggestions() == 1
            result.get(1).getTotalNumberSuggestions() == 2
        }
    }

    @TestConfiguration
    static class StatisticsServiceImplTestContextConfiguration {
        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
