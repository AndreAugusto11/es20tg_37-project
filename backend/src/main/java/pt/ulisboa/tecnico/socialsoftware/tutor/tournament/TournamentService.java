package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
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
				DateHandler.toLocalDateTime(tournamentDto.getStartTime()),
				DateHandler.toLocalDateTime(tournamentDto.getEndTime())
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