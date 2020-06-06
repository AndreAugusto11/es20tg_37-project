package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class AnswerTournamentQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TIME_TAKEN = 1234
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0
    public static final String URL = 'URL'

    @Autowired
    TournamentService tournamentService

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

    def user
    def tournament
    def course
    def courseExecution
    def question
    def quizQuestion
    def option
    def quizAnswer
    def quiz

    def setup() {
        user = new User("Manel1", "Man12", 1, User.Role.STUDENT)

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.getUsers().add(user)
        courseExecutionRepository.save(courseExecution)

        user.getCourseExecutions().add(courseExecution)
        userRepository.save(user)

        question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        option = new Option()
        option.setSequence(SEQUENCE)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        optionRepository.save(option)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        quiz.setOneWay(false)
        courseExecution.addQuiz(quiz)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question, SEQUENCE)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer(user, quiz)
        quizAnswerRepository.save(quizAnswer)

        tournament = new Tournament()
        tournament.setCreator(user)
        tournament.setQuiz(quiz)
        tournament.setStartTime(LocalDateTime.now())
        tournament.setEndTime(LocalDateTime.now().plusDays(1))
        tournament.setStatus(Tournament.Status.ONGOING)
        tournamentRepository.save(tournament)
    }

    def "Answer Quiz of Open/Closed Tournament"(){
        given:"questionAnswer"
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        def statementAnswer = new StatementAnswerDto(questionAnswer)

        and:"Open/Closed Tournament"
        tournament.setStatus(Tournament.Status.ENROLLING)

        when:
        tournamentService.submitAnswer(user.getId(),tournament.getId(),statementAnswer)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NOT_ONGOING

    }

    def "Answer Quiz of Tournament with no generated quiz" () {
        given:"questionAnswer"
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        def statementAnswer = new StatementAnswerDto(questionAnswer)

        and:"tournament quiz null"
        tournament.setQuiz(null)

        when:
        tournamentService.submitAnswer(user.getId(),tournament.getId(),statementAnswer)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NULL_QUIZ
    }

    def "Answer Quiz of Tournament by Unenrolled student" () {
        given:"questionAnswer"
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        def statementAnswer = new StatementAnswerDto(questionAnswer)

        and:"non enrolled student"
        def user1 = new User("Manel12", "Man123", 2, User.Role.STUDENT)
        courseExecution.getEnrolledUsers().add(user)

        user1.getCourseExecutions().add(courseExecution)
        userRepository.save(user1)

        when:
        tournamentService.submitAnswer(user1.getId(),tournament.getId(),statementAnswer)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_STUDENT_NOT_ENROLLED
    }

    //TODO fazer teste quando geração de quizes tiver funcional
    /*
    def "Start Quiz of an Ongoing Tournament"() {
        when:
        def res = tournamentService.startQuiz(user.getId(),tournament.getId())

        then:
        res
    }*/

    def "Submit Answer to an Ongoing Tournament Quiz"() {
        given:"questionAnswer"
        def questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, TIME_TAKEN, option, SEQUENCE)

        and:"StatementAnswerDto"
        def statementAnswer = new StatementAnswerDto(questionAnswer)

        when:
        tournamentService.submitAnswer(user.getId(),tournament.getId(),statementAnswer)

        then:
        def quizAns = user.getQuizAnswers().stream()
                .filter({ qa -> qa.getQuiz().getId().equals(quiz.getId())})
                .findFirst().get()
        def quesAnsw = quizAns.getQuestionAnswers().stream()
                .filter({ qa -> qa.getSequence().equals(statementAnswer.getSequence()) })
                .findFirst().get()

        quesAnsw.getTimeTaken() == questionAnswer.getTimeTaken()
    }

    def "Conclude tournament quiz with one correct answer"() {
        when:
        def results = tournamentService.concludeQuiz(user.getId(),tournament.getId())

        then:
        results.size() == 1
    }

    @TestConfiguration
    static class TournamentServiceSubmitTestContextConfiguration {
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