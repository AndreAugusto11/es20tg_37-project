package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {
	@Autowired
	private TournamentRepository tournamentRepository;

	@Autowired
	private CourseExecutionRepository courseExecutionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TopicRepository topicRepository;

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public CourseDto findTournamentCourseExecution(int tournamentId) {

		return this.tournamentRepository.findById(tournamentId)
				.map(Tournament::getCourseExecution)
				.map(CourseDto::new)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> findTournaments(int executionId) {

		courseExecutionRepository.findById(executionId)
				.orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

		return tournamentRepository.findByCourseExecutionId(executionId).stream()
				.sorted(Comparator.comparing(Tournament::getAvailableDate, Comparator.nullsFirst(Comparator.reverseOrder())))
				.sorted(Comparator.comparing(Tournament::getStatus))
				.peek(tournament -> {
					if (tournament.getStatus() != Tournament.Status.CANCELLED)
						tournament.updateStatus();
				})
				.map(TournamentDto::new)
				.collect(Collectors.toList());
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(int userId, int executionId, TournamentDto tournamentDto) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		CourseExecution courseExecution = courseExecutionRepository.findById(executionId)
				.orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

		if (tournamentDto == null)
			throw new TutorException(INVALID_NULL_ARGUMENTS_TOURNAMENTDTO);

		Set<TopicConjunction> topicConjunctions = tournamentDto.getTopicConjunctions().stream()
				.map(topicConjunctionDto -> {
					TopicConjunction topicConjunction = new TopicConjunction();

					Set<Topic> newTopics = topicConjunctionDto.getTopics().stream()
							.map(topicDto -> topicRepository.findById(topicDto.getId())
									.orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND)))
							.collect(Collectors.toSet());

					topicConjunction.updateTopics(newTopics);
					return topicConjunction;
				}).collect(Collectors.toSet());

		if (topicConjunctions.isEmpty())
			throw new TutorException(TOURNAMENT_HAS_NO_TOPICS);

		if (tournamentDto.getCreationDate() == null)
			tournamentDto.setCreationDate(DateHandler.toISOString(DateHandler.now()));

		Tournament tournament = new Tournament(courseExecution, user, topicConjunctions, tournamentDto);

		tournamentRepository.save(tournament);
		return new TournamentDto(tournament);
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto enrollInTournament(int userId, int tournamentId) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		if (user.getRole() != User.Role.STUDENT)
			throw new TutorException(USER_IS_STUDENT, userId);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

		if (!user.getCourseExecutions().contains(tournament.getCourseExecution()))
			throw new TutorException(USER_NOT_ENROLLED, user.getUsername());

		if (tournament.getStatus() != Tournament.Status.ENROLLING)
			throw new TutorException(TOURNAMENT_ENROLLMENTS_NO_LONGER_AVAILABLE);

		if (tournament.getEnrolledUsers().contains(user))
			throw new TutorException(STUDENT_ALREADY_ENROLLED_IN_TOURNAMENT, userId);

		tournament.addEnrolledUser(user);
		return new TournamentDto(tournament);

		//TODO Check use of this code in jmeter
		/*
		Predicate<User> u1 = s -> s.getId().equals(userId);

		//DEMO STUDENT has id 676 needed for load test
		if(userId != 676 && tournament.getUsers().stream().anyMatch(u1))
			throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED, userId);
		*/
	}

	@Retryable(
			value = { SQLException.class },
			backoff = @Backoff(delay = 5000))
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void cancelTournament(int userId, int tournamentId) {

		userRepository.findById(userId)
				.orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

		if (userId != tournament.getCreator().getId())
			throw new TutorException(TOURNAMENT_NON_CREATOR, tournamentId, userId);

		if (tournament.getStatus() != Tournament.Status.ENROLLING)
			throw new TutorException(TOURNAMENT_NOT_ENROLLING, tournamentId);

		tournament.setStatus(Tournament.Status.CANCELLED);
	}

	/*private void generateQuiz(Tournament tournament, int courseExecutionId) {
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

		if (tournament.getStatus() != Tournament.Status.OPEN)
			throw new TutorException(TOURNAMENT_NOT_ONGOING, tournamentId);

		if (tournament.getQuiz() == null)
			throw new TutorException(TOURNAMENT_NULL_QUIZ, tournamentId);

		tournament.getEnrolledUsers().stream()
				.filter(u -> u.getId().equals(userId))
				.findFirst()
				.orElseThrow(() -> new TutorException(TOURNAMENT_STUDENT_NOT_ENROLLED, userId));

		return tournament;
	}*/
}