package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {
	@Autowired
	private TournamentRepository tournamentRepository;

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private CourseExecutionRepository courseExecutionRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Autowired
	private StatementService statementService;

	@PersistenceContext
	EntityManager entityManager;

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(int userId, TournamentDto tournamentDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		if (tournamentDto.getTopics() == null)
			throw new TutorException(TOURNAMENT_WITH_NULL_TOPICS);

		Set<Topic> topics = tournamentDto.getTopics().stream()
				.map(topic -> topicRepository.findById(topic.getId())
						.orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topic.getId())))
				.collect(Collectors.toSet());

		Tournament tournament = new Tournament(
				user,
				topics,
				tournamentDto.getNumberQuestions(),
				DateHandler.toLocalDateTime(tournamentDto.getStartTime()).plusHours(1),
				DateHandler.toLocalDateTime(tournamentDto.getEndTime()).plusHours(1)
		);

		user.addCreatedTournament(tournament);

		this.entityManager.persist(tournament);
		return new TournamentDto(tournament);
	}

	private Tournament findTournamentById(int tournamentId) {
		return tournamentRepository.findById(tournamentId)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto enrollStudentInTournament(int userId, int tournamentId) {

		Tournament tournament = this.findTournamentById(tournamentId);

		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND, userId));

		if (user.getRole() != User.Role.STUDENT)
			throw new TutorException(TOURNAMENT_NOT_STUDENT);

		if (tournament.getStatus() != Tournament.Status.CREATED)
			throw new TutorException(TOURNAMENT_NOT_CREATED, tournamentId);

		if (tournament.getUsers().stream().anyMatch(u -> u.getId().equals(userId)))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED, userId);

		//TODO Check use of this code in jmeter
		/*
		Predicate<User> u1 = s -> s.getId().equals(userId);

		//DEMO STUDENT has id 676 needed for load test
		if(userId != 676 && tournament.getUsers().stream().anyMatch(u1))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED, userId);
		*/

		tournament.addUser(user);

		return new TournamentDto(tournament);
	}

	// DUPLICATED CODE: Course Execution is missing from implementation !!
	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto enrollStudentInTournament1(int userId, int tournamentId, int courseExecutionId) {

		Tournament tournament = this.findTournamentById(tournamentId);

		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND, userId));

		if (user.getRole() != User.Role.STUDENT)
			throw new TutorException(TOURNAMENT_NOT_STUDENT);

		if (tournament.getStatus() != Tournament.Status.CREATED)
			throw new TutorException(TOURNAMENT_NOT_CREATED, tournamentId);

		if (tournament.getUsers().stream().anyMatch(u -> u.getId().equals(userId)))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED, userId);

		if (tournament.getQuiz() == null)
			generateQuiz(tournament, courseExecutionId);

		//TODO Check use of this code in jmeter
		/*
		Predicate<User> u1 = s -> s.getId().equals(userId);

		//DEMO STUDENT has id 676 needed for load test
		if(userId != 676 && tournament.getUsers().stream().anyMatch(u1))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED, userId);
		*/

		tournament.addUser(user);

		return new TournamentDto(tournament);
	}

	private void generateQuiz(Tournament tournament, int courseExecutionId) {
		CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
		Quiz quiz = initializeQuiz(tournament);

		List<Question> questions = questionRepository.
				findAvailableQuestionsFromTopics(tournament.getTopics().stream()
						.map(Topic::getId)
						.collect(Collectors.toList()));

		quiz.setCreationDate(DateHandler.now());
		quiz.setCourseExecution(courseExecution);

		for (int i = 0; i < tournament.getNumberQuestions(); i++) {
			Random rand = new Random();
			Question question = questions.get(rand.nextInt(questions.size()));
			new QuizQuestion(quiz, question, quiz.getQuizQuestions().size());
		}

		tournament.setQuiz(quiz);
		quiz.setTournament(tournament);

		quizRepository.save(quiz);
	}

	private Quiz initializeQuiz(Tournament tournament) {
		Quiz quiz = new Quiz();
		quiz.setType(Quiz.QuizType.GENERATED.name());
		quiz.setKey(getMaxQuizKey() + 1);
		quiz.setScramble(true);
		quiz.setOneWay(false);
		quiz.setQrCodeOnly(false);
		quiz.setOneWay(false);
		quiz.setAvailableDate(tournament.getStartTime());
		quiz.setConclusionDate(tournament.getEndTime());

		return quiz;
	}

	private Integer getMaxQuizKey() {
		Integer maxQuizKey = quizRepository.getMaxQuizKey();
		return maxQuizKey != null ? maxQuizKey : 0;
	}

	private Tournament tournamentQuizVerification(int userId, int tournamentId) {
		Tournament tournament = this.findTournamentById(tournamentId);

		if (tournament.getStatus() != Tournament.Status.ONGOING)
			throw new TutorException(TOURNAMENT_NOT_ONGOING, tournamentId);

		if (tournament.getQuiz() == null)
			throw new TutorException(TOURNAMENT_NULL_QUIZ, tournamentId);

		tournament.getUsers().stream()
				.filter(u -> u.getId().equals(userId))
				.findFirst()
				.orElseThrow(() -> new TutorException(TOURNAMENT_STUDENT_NOT_ENROLLED, userId));

		return tournament;
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void startQuiz(int userId, int tournamentId) {
		Tournament tournament = tournamentQuizVerification(userId, tournamentId);
		statementService.startQuiz(userId, tournament.getQuiz().getId());
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void submitAnswer(int userId, int tournamentId, StatementAnswerDto answer) {
		Tournament tournament = tournamentQuizVerification(userId,tournamentId);
		statementService.submitAnswer(userId, tournament.getQuiz().getId(), answer);
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public List<CorrectAnswerDto> concludeQuiz(int userId, int tournamentId) {
		Tournament tournament = tournamentQuizVerification(userId,tournamentId);
		return statementService.concludeQuiz(userId, tournament.getQuiz().getId());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getTournaments() {

		//TODO generate quiz here

		tournamentRepository.findAll().forEach(Tournament::statusUpdate);

		//TODO fix nullPointerException when also comparing by startTimes
		return tournamentRepository.findAll().stream()
				.map(TournamentDto::new)
				.sorted(Comparator.comparing(TournamentDto::getStatus))
				.collect(Collectors.toList());
	}

	@Deprecated
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getEnrolledTournaments(int userId) {

		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND,userId));

		tournamentRepository.findAll().forEach(Tournament::statusUpdate);

		return user.getEnrolledTournaments().stream()
				.map(TournamentDto::new)
				.sorted(Comparator
						.comparing(TournamentDto::getId))
				.collect(Collectors.toList());
	}

	@Deprecated
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getCreatedTournaments(Integer userId) {
		if(userId == null) throw new TutorException(TOURNAMENT_NULL_USER);
		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND,userId));
		tournamentRepository.findAll().forEach(Tournament::statusUpdate);
		return user.getCreatedTournaments().stream()
				.map(TournamentDto::new)
				.sorted(Comparator
						.comparing(TournamentDto::getId))
				.collect(Collectors.toList());
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void cancelTournament(int userId, int tournamentId) {
		Tournament tournament = this.findTournamentById(tournamentId);

		if (tournament.getStatus() == Tournament.Status.CANCELLED)
			throw new TutorException(TOURNAMENT_ALREADY_CANCELLED, tournamentId);

		//TODO Add test for this
		if (tournament.getStatus() == Tournament.Status.ONGOING)
			throw new TutorException(TOURNAMENT_ALREADY_ONGOING, tournamentId);

		userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		int creatorId = tournament.getCreator().getId();

		if (creatorId != userId)
			throw new TutorException(TOURNAMENT_NON_CREATOR, tournamentId, userId);

		tournament.setStatus(Tournament.Status.CANCELLED);
	}
}