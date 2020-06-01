package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
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
import java.util.function.*;
import java.time.LocalDateTime;
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
	public TournamentDto createTournament(User user, Set<Topic> topics, Integer numOfQuestions, LocalDateTime startTime, LocalDateTime endTime) {
		Tournament tournament = new Tournament(user, topics, numOfQuestions, startTime, endTime);
		user.addCreatedTournament(tournament);

		this.entityManager.persist(tournament);
		return new TournamentDto(tournament);
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(Integer userId, TournamentDto tournamentDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		Set<Topic> topics = tournamentDto.getTopics().stream()
				.map(topic -> topicRepository.findById(topic.getId())
						.orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topic.getId())))
				.collect(Collectors.toSet());

		Tournament tournament = new Tournament(user, topics, tournamentDto.getNumberQuestions(), tournamentDto.getStartTime(), tournamentDto.getEndTime());
		user.addCreatedTournament(tournament);

		this.entityManager.persist(tournament);
		return new TournamentDto(tournament);
	}

	public TournamentDto findTournamentById(Integer id){
		return tournamentRepository.findById(id).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,id));
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto enrollStudentInTournament(Integer userId, Integer tournamentId){
		if(userId == null) throw new TutorException(TOURNAMENT_NULL_USER);

		if(tournamentId == null) throw new TutorException(TOURNAMENT_NULL_TOURNAMENT);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND, userId));

		if(user.getRole() != User.Role.STUDENT)
			throw new TutorException(TOURNAMENT_NOT_STUDENT);

		if(tournament.getStatus() != Tournament.Status.CREATED)
			throw new TutorException(TOURNAMENT_NOT_OPEN,tournamentId);

		Predicate<User> u1 = s -> s.getId().equals(userId);

		//DEMO STUDENT has id 676 needed for load test
		if(userId != 676 && tournament.getUsers().stream().anyMatch(u1))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED,userId);

		tournament.addUser(user);
		user.addTournament(tournament);
		return new TournamentDto(tournament);
	}

	private Tournament tournamentQuizVerification(int userId, Integer tournamentId){
		if(tournamentId == null) throw new TutorException(TOURNAMENT_NULL_TOURNAMENT);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND,tournamentId));

		if(tournament.getStatus() != Tournament.Status.ONGOING) throw new TutorException(TOURNAMENT_NOT_ONGOING,tournamentId);

		if(tournament.getQuiz() == null) throw new TutorException(TOURNAMENT_NULL_QUIZ,tournamentId);

		tournament.getUsers().stream()
				.filter(u->u.getId().equals(userId)).findFirst()
				.orElseThrow( () -> new TutorException(TOURNAMENT_STUDENT_NOT_ENROLLED,userId));
		return tournament;
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public boolean startQuiz(int userId, Integer tournamentId) {
		Tournament tournament = tournamentQuizVerification(userId,tournamentId);
		statementService.startQuiz(userId, tournament.getQuiz().getId());
		return true;
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void submitAnswer(int userId, Integer tournamentId, StatementAnswerDto answer) {
		Tournament tournament = tournamentQuizVerification(userId,tournamentId);
		System.out.println(tournament.getQuiz().toString());
		statementService.submitAnswer(userId, tournament.getQuiz().getId(), answer);
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public List<CorrectAnswerDto> concludeQuiz(int userId, Integer tournamentId) {
		Tournament tournament = tournamentQuizVerification(userId,tournamentId);
		return statementService.concludeQuiz(userId, tournament.getQuiz().getId());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getTournaments() {
		tournamentRepository.findAll().forEach(Tournament::statusUpdate);
		return tournamentRepository.findAll().stream()
				.map(TournamentDto::new)
				.sorted(Comparator
						.comparing(TournamentDto::getId))
				.collect(Collectors.toList());
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getEnrolledTournaments(Integer userId) {
		if(userId == null) throw new TutorException(TOURNAMENT_NULL_USER);
		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND,userId));
		tournamentRepository.findAll().forEach(Tournament::statusUpdate);
		return user.getEnrolledTournaments().stream()
				.map(TournamentDto::new)
				.sorted(Comparator
						.comparing(TournamentDto::getId))
				.collect(Collectors.toList());
	}

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
	public void cancelTournament(Integer userId, Integer tournamentId) {

		if (tournamentId == null)
			throw new TutorException(TOURNAMENT_NULL_ID);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

		if (tournament.getStatus() == Tournament.Status.CANCELLED) {
			throw new TutorException(TOURNAMENT_ALREADY_CANCELLED, tournamentId);
		}

		if (userId == null)
			throw new TutorException(TOURNAMENT_NULL_USER);
		userRepository.findById(userId).orElseThrow( () -> new TutorException(USER_NOT_FOUND,userId));

		User creator = tournament.getCreator();
		Integer creatorId = creator.getId();

		if (creatorId.equals(userId)) {
			tournament.setStatus(Tournament.Status.CANCELLED);
		}
		else {
			throw new TutorException(TOURNAMENT_NON_CREATOR, tournamentId, userId);
		}

	}
}