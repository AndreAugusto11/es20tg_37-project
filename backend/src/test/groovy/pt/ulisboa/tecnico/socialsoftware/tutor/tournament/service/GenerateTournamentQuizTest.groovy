package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_ENOUGH_QUESTIONS
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.STUDENT_NOT_ENROLLED_IN_TOURNAMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_ALREADY_COMPLETED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_ONGOING
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_ENROLLED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class GenerateTournamentQuizTest extends Specification {

    public static final String QUESTION1_TITLE = "Question 1 Title"
    public static final String QUESTION1_CONTENT = "Question 1 Content"
    public static final String QUESTION2_TITLE = "Question 2 Title"
    public static final String QUESTION2_CONTENT = "Question 2 Content"
    public static final String OPTION_CONTENT = "Option Content"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_ACRONYM = "SA1"
    public static final String ACADEMIC_TERM = "First Semester"
    public static final String CREATOR_NAME = "Creator Name"
    public static final String CREATOR_USERNAME = "Creator Username"
    public static final String ENROLLER_NAME = "Enroller Name"
    public static final String ENROLLER_USERNAME = "Enroller Username"
    public static final String TOPIC1_NAME = "Topic 1 Name"
    public static final String TOPIC2_NAME = "Topic 2 Name"
    public static final String TOPIC3_NAME = "Topic 3 Name"
    public static final String TOURNAMENT_TITLE = "Tournament Title"
    public static final int NUMBER_OF_QUESTIONS = 2

    @Autowired
    StatementService statementService

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TopicConjunctionRepository topicConjunctionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    def course = new Course()
    def courseExecution = new CourseExecution()
    def creator = new User()
    def enroller = new User()
    def topic1 = new Topic()
    def topic2 = new Topic()
    def topic3 = new Topic()
    def question1 = new Question()
    def question2 = new Question()
    def topicConjunction1 = new TopicConjunction()
    def topicConjunction2 = new TopicConjunction()
    def tournament = new Tournament()

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

        enroller.setName(ENROLLER_NAME)
        enroller.setUsername(ENROLLER_USERNAME)
        enroller.setKey(2)
        enroller.setRole(User.Role.STUDENT)
        enroller.addCourseExecutions(courseExecution)
        userRepository.save(enroller)

        topic1.setName(TOPIC1_NAME)
        topic1.setCourse(course)

        topic2.setName(TOPIC2_NAME)
        topic2.setCourse(course)

        topic3.setName(TOPIC3_NAME)
        topic3.setCourse(course)

        question1.setKey(1)
        question1.setCourse(course)
        question1.setTitle(QUESTION1_TITLE)
        question1.setContent(QUESTION1_CONTENT)
        question1.setType(Question.Type.NORMAL)
        question1.setStatus(Question.Status.AVAILABLE)
        question1.addTopic(topic1)
        question1.addTopic(topic2)
        question1.addTopic(topic3)

        def option1 = new Option()
        option1.setContent(OPTION_CONTENT)
        option1.setCorrect(true)
        option1.setSequence(0)
        option1.setQuestion(question1)
        questionRepository.save(question1)

        question2.setKey(2)
        question2.setCourse(course)
        question2.setTitle(QUESTION2_TITLE)
        question1.setContent(QUESTION2_CONTENT)
        question2.setType(Question.Type.NORMAL)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.addTopic(topic1)
        question2.addTopic(topic3)

        def option2 = new Option()
        option2.setContent(OPTION_CONTENT)
        option2.setCorrect(true)
        option2.setSequence(0)
        option2.setQuestion(question2)
        questionRepository.save(question2)

        topicConjunction1.addTopic(topic1)
        topicConjunction1.addTopic(topic2)
        topicConjunction1.addTopic(topic3)
        topic1.addTopicConjunction(topicConjunction1)
        topic2.addTopicConjunction(topicConjunction1)
        topic3.addTopicConjunction(topicConjunction1)

        topicConjunction2.addTopic(topic1)
        topicConjunction2.addTopic(topic3)
        topic1.addTopicConjunction(topicConjunction2)
        topic3.addTopicConjunction(topicConjunction2)

        def topicConjunctions = new HashSet()
        topicConjunctions.add(topicConjunction1)
        topicConjunctions.add(topicConjunction2)

        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setCourseExecution(courseExecution)
        tournament.setCreator(creator)
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournament.setAvailableDate(DateHandler.now())
        tournament.setConclusionDate(DateHandler.now().plusDays(1))
        tournament.setResultsDate(DateHandler.now().plusDays(2))
        tournament.setStatus(Tournament.Status.ONGOING)
        tournament.setTopicConjunctions(topicConjunctions)
        tournament.addEnrolledUser(creator)
        tournamentRepository.save(tournament)
    }

    def "The first student to enter the tournament generates the tournament quiz"() {
        given: "an enrolled student"
        tournament.addEnrolledUser(enroller)

        when: "a user tries to generate the quiz"
        def result = statementService.getTournamentQuiz(enroller.getId(), tournament.getId())

        then: "a quiz was generated for the tournament"
        quizRepository.count() == 1L
        result != null
        def resultQuiz = quizRepository.findAll().get(0)
        resultQuiz != null
        resultQuiz.getId() == result.getId()
        resultQuiz.getCourseExecution() == courseExecution
        resultQuiz.getTitle() == TOURNAMENT_TITLE
        courseExecution.getQuizzes().contains(resultQuiz)
        resultQuiz.getType() == Quiz.QuizType.GENERATED
        resultQuiz.getCreationDate() != null
        resultQuiz.getAvailableDate() != null
        resultQuiz.getConclusionDate() != null
        resultQuiz.getResultsDate() != null
        resultQuiz.getTournament() == tournament
        tournament.getQuiz() == resultQuiz
        resultQuiz.getQuizQuestions().size() == 2

        and: "A quiz answer for the student exists"
        quizAnswerRepository.count() == 1L
        def resultQuizAnswer = quizAnswerRepository.findAll().get(0)
        resultQuizAnswer.getId() == result.getQuizAnswerId()
        resultQuizAnswer.getUser() == enroller
        enroller.getQuizAnswers().contains(resultQuizAnswer)
        resultQuizAnswer.getQuiz() == resultQuiz
        resultQuiz.getQuizAnswers().contains(resultQuizAnswer)
    }

    def "A tournament only generates a quiz once"() {
        given: "an enrolled student"
        tournament.addEnrolledUser(enroller)

        and: "a user that generates the tournament quiz"
        def quizDto = statementService.getTournamentQuiz(creator.getId(), tournament.getId())

        when: "a second tries to generate the tournament quiz"
        def result = statementService.getTournamentQuiz(enroller.getId(), tournament.getId())

        then: "the returned quiz is the same that was previously generated"
        quizRepository.count() == 1L
        result != null
        quizDto != null
        result.getId() == quizDto.getId()

        and: "a new quiz answer was generated"
        quizAnswerRepository.count() == 2L
        result.getQuizAnswerId() != quizDto.getQuizAnswerId()
        quizAnswerRepository.findQuizAnswer(result.getId(), creator.getId()).isPresent()
        quizAnswerRepository.findQuizAnswer(result.getId(), enroller.getId()).isPresent()
    }

    def "Cannot generate tournament quiz given invalid user id"() {
        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(0, tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_FOUND
    }

    def "Cannot generate tournament quiz given invalid tournament id"() {
        when: "the tournament generates the quiz"
        statementService.getTournamentQuiz(enroller.getId(), 0)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Cannot generate tournament quiz if the student does not belong to the tournament course execution"() {
        given: "a student that does not belong to the course execution"
        enroller.setCourseExecutions(new HashSet<CourseExecution>())

        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(enroller.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == USER_NOT_ENROLLED
    }

    def "Cannot generate tournament quiz if the student did not enroll in the tournament"() {
        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(enroller.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == STUDENT_NOT_ENROLLED_IN_TOURNAMENT
    }

    @Unroll
    def "Cannot generate tournament quiz of a tournament that is #test"() {
        given: "a tournament that is #test"
        tournament.setStatus(status)

        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(creator.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == errorMessage

        where:
        test        | status                      || errorMessage
        "enrolling" | Tournament.Status.ENROLLING || TOURNAMENT_NOT_ONGOING
        "concluded" | Tournament.Status.CONCLUDED || TOURNAMENT_NOT_ONGOING
        "cancelled" | Tournament.Status.CANCELLED || TOURNAMENT_NOT_ONGOING
    }

    def "Cannot generate tournament quiz if there are not enough questions"() {
        given: "a number of questions superior to the number of available questions"
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS + 1)

        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(creator.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == NOT_ENOUGH_QUESTIONS
    }

    def "Cannot generate tournament quiz if the student has already completed the tournament"() {
        given: "an enrolled user"
        tournament.addEnrolledUser(enroller)

        and: "a completed quiz answer"
        def quizDto = statementService.getTournamentQuiz(enroller.getId(), tournament.getId())
        quizDto != null
        def quizAnswer = quizAnswerRepository.findById(quizDto.getQuizAnswerId()).orElse(null)
        quizAnswer != null
        quizAnswer.setCompleted(true)

        when: "a user tries to generate the quiz"
        statementService.getTournamentQuiz(enroller.getId(), tournament.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == TOURNAMENT_ALREADY_COMPLETED
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
