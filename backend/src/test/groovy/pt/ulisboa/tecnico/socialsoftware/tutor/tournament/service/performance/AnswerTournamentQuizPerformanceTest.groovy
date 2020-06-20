package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service.performance

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class AnswerTournamentQuizPerformanceTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final LocalDateTime TOURNAMENT_AVAILABLE_DATE = DateHandler.now().minusDays(1)
    public static final LocalDateTime TOURNAMENT_QUIZ_ANSWER_CREATION_DATE = DateHandler.now()


    @Autowired
    StatementService statementService

    @Autowired
    TournamentRepository tournamentRepository

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

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def quiz = new Quiz()
    def quizAnswer = new QuizAnswer()
    def questionAnswer = new QuestionAnswer()
    def question = new Question()
    def option = new Option()

    def setup() {
        course.setName(COURSE_NAME)
        course.setType(Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution.setCourse(course)
        courseExecution.setType(Course.Type.TECNICO)
        courseExecution.setAcronym(COURSE_ACRONYM)
        courseExecution.setAcademicTerm(ACADEMIC_TERM)
        courseExecutionRepository.save(courseExecution)

        creator.setName(CREATOR_NAME)
        creator.setUsername(CREATOR_USERNAME)
        creator.setKey(1)
        creator.setRole(User.Role.STUDENT)
        creator.addCourseExecutions(courseExecution)
        userRepository.save(creator)

        question.setKey(1)
        question.setCourse(course)
        question.setContent(QUESTION_CONTENT)
        question.setTitle(QUESTION_TITLE)
        questionRepository.save(question)

        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestion(question)
        optionRepository.save(option)

        quiz.setKey(1)
        quiz.setTitle(TOURNAMENT_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        quiz.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setSequence(1)
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)

        quizAnswer.setQuiz(quiz)
        quizAnswer.setUser(creator)
        quizAnswer.setCreationDate(TOURNAMENT_QUIZ_ANSWER_CREATION_DATE)
        quizAnswer.setCompleted(false)

        questionAnswer.setSequence(0)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
    }

    def "performance testing to answer quiz in 10000 tournaments"(){
        given: "10000 tournaments"
        1.upto(1, {
            def tournament = new Tournament()
            tournament.setTitle(TOURNAMENT_TITLE)
            tournament.setCourseExecution(courseExecution)
            tournament.setCreator(creator)
            tournament.setAvailableDate(TOURNAMENT_AVAILABLE_DATE)
            tournament.setStatus(Tournament.Status.ONGOING)
            tournament.setQuiz(quiz)
            tournamentRepository.save(tournament)
        })
        List<Tournament> tournamentList = tournamentRepository.findAll()

        and: "An answer"
         def answer = new StatementAnswerDto(questionAnswer)

        when:
        1.upto(1, {
            statementService.submitAnswer(creator.getId(), tournamentList.pop().getId(), answer)
        })

        then:
        true
    }


    @TestConfiguration
    static class TournamentServiceCreatTestContextConfiguration {
        @Bean
        QuestionService QuestionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport AnswersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuizService QuizService() {
            return new QuizService()
        }

        @Bean
        AnswerService AnswerService() {
            return new AnswerService()
        }

        @Bean
        StatementService statementService() {
            return new StatementService()
        }

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}