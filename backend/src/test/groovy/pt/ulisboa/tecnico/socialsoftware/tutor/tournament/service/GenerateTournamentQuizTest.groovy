package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GenerateTournamentQuizTest extends Specification {
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    private static final String TOPIC_ONE = "topicOne"
    private static final String TOPIC_TWO = "topicTwo"
    private static final String TOPIC_THREE = "topicThree"
    private static final Integer NUMBER_QUESTIONS = 2
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    UserRepository userRepository


    def courseExecution
    def student
    def tournament
    def startDate
    def endDate
    def question
    def question1

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User('student', "studentName", 1, User.Role.STUDENT)
        userRepository.save(student)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.getUsers().add(student)
        courseExecutionRepository.save(courseExecution)

        def topicOne = new Topic()
        topicOne.setName(TOPIC_ONE)
        topicOne.setCourse(course)
        topicRepository.save(topicOne);

        def topicTwo = new Topic()
        topicTwo.setName(TOPIC_TWO)
        topicTwo.setCourse(course)
        topicRepository.save(topicTwo);

        def topicThree = new Topic()
        topicThree.setName(TOPIC_THREE)
        topicThree.setCourse(course)
        topicRepository.save(topicThree);

        def topics = new HashSet<Topic>()
        topics.add(topicOne)
        topics.add(topicTwo)
        topics.add(topicThree)

        question = new Question()
        question.setCourse(course)
        course.addQuestion(question)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        def option = new Option()
        option.setSequence(SEQUENCE)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question)
        question.addOption(option)
        optionRepository.save(option)
        question.addTopic(topicOne)
        topicOne.addQuestion(question)

        question1 = new Question()
        question1.setCourse(course)
        course.addQuestion(question1)
        question1.setTitle(QUESTION_TITLE + '1')
        question1.setContent(QUESTION_CONTENT + '1')
        question1.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        def option1 = new Option()
        option1.setSequence(SEQUENCE)
        option1.setContent(OPTION_CONTENT)
        option1.setCorrect(true)
        option1.setQuestion(question)
        question1.addOption(option1)
        optionRepository.save(option1)
        question1.addTopic(topicTwo)
        topicTwo.addQuestion(question1)

        tournament = new Tournament()
        tournament.setCreator(student)
        tournament.setNumberQuestions(NUMBER_QUESTIONS)
        tournament.setTopics(topics)
        startDate = LocalDateTime.now()
        tournament.setStartTime(startDate)
        endDate = LocalDateTime.now().plusDays(1)
        tournament.setEndTime(endDate)
        tournament.setStatus(Tournament.Status.ENROLLING)
        tournamentRepository.save(tournament)
    }

    def "student enrolls in tournament and quiz is generated"() {
        given: "a student"
        def user2 = new User("Manel2","MAN123",2, User.Role.STUDENT)
        userRepository.save(user2)

        when:
        tournamentService.enrollStudentInTournament1(user2.getId(),tournament.getId(), courseExecution.getId())

        then: "User is enrolled correctly"
        tournament.getEnrolledUsers().size() == 2
        user2.getEnrolledTournaments().size() == 1
        tournament.getStatus() == Tournament.Status.ENROLLING
        tournament.getEnrolledUsers().stream().anyMatch({ u -> u.getId() == user2.getId() })
        user2.getEnrolledTournaments().stream().anyMatch({ t -> t.getId() == tournament.getId() })
        and: "quiz gets generated"
        tournament.getQuiz() != null
        tournament.getQuiz().getQuizQuestions()[0].getQuestion() == question || tournament.getQuiz().getQuizQuestions()[0].getQuestion() == question1
        tournament.getQuiz().getAvailableDate() == startDate
        tournament.getQuiz().getConclusionDate() == endDate
        tournament.getQuiz().getTournamentId() == tournament
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
