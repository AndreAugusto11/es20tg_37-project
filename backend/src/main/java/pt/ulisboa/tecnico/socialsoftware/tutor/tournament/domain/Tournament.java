package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.TopicConjunction;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

	private static final int MINIMUM_ENROLLMENTS = 2;

	public enum Status {
		ONGOING, ENROLLING, CONCLUDED, CANCELLED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "course_execution_id")
	private CourseExecution courseExecution;

	@Column(nullable = false)
	private String title;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User creator;

	@Column(name = "number_of_questions", columnDefinition = "integer default 0")
	private int numberOfQuestions;

	@Column(name = "creation_date")
	private LocalDateTime creationDate;

	@Column(name = "available_date")
	private LocalDateTime availableDate;

	@Column(name = "conclusion_date")
	private LocalDateTime conclusionDate;

	@Column(name = "results_date")
	private LocalDateTime resultsDate;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ENROLLING;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tournament", fetch=FetchType.EAGER, orphanRemoval=true)
	private Set<TopicConjunction> topicConjunctions = new HashSet<>();

	@ManyToMany
	private Set<User> enrolledUsers = new HashSet<>();

	@OneToOne(cascade = CascadeType.ALL)
	private Quiz quiz;

	public Tournament() {}

	public Tournament(CourseExecution courseExecution, User creator, Set<TopicConjunction> topicConjunctions, TournamentDto tournamentDto) {
		setCourseExecution(courseExecution);
		setTitle(tournamentDto.getTitle());
		setCreator(creator);
		setNumberOfQuestions(tournamentDto.getNumberOfQuestions());
		setCreationDate(DateHandler.toLocalDateTime(tournamentDto.getCreationDate()));
		setAvailableDate(DateHandler.toLocalDateTime(tournamentDto.getAvailableDate()));
		setConclusionDate(DateHandler.toLocalDateTime(tournamentDto.getConclusionDate()));
		setResultsDate(DateHandler.toLocalDateTime(tournamentDto.getResultsDate()));

		if (tournamentDto.getStatus() == null)
			setStatus(Status.ENROLLING);
		else
			setStatus(Tournament.Status.valueOf(tournamentDto.getStatus()));

		setTopicConjunctions(topicConjunctions);

		if (numberOfQuestions > getQuestions().size())
			throw new TutorException(NOT_ENOUGH_QUESTIONS);
	}

	public Integer getId() { return this.id; }

	public CourseExecution getCourseExecution() {
		return courseExecution;
	}

	public void setCourseExecution(CourseExecution courseExecution) {
		this.courseExecution = courseExecution;
		courseExecution.addTournament(this);
	}

	public String getTitle() { return title; }

	public void setTitle(String title) {
		if (title == null || title.isBlank())
			throw new TutorException(INVALID_TITLE_FOR_TOURNAMENT);

		this.title = title;
	}

	public User getCreator() {
		return this.creator;
	}

	public void setCreator(User creator) {
		if (creator == null)
			throw new TutorException(TOURNAMENT_NULL_USER);

		this.creator = creator;
		creator.addCreatedTournament(this);

		this.enrolledUsers.add(creator);
		creator.addEnrolledTournament(this);
	}

	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(int numberOfQuestions) {
		if (numberOfQuestions <= 0)
			throw new TutorException(INVALID_NUMBER_OF_QUESTIONS);
		this.numberOfQuestions = numberOfQuestions;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getAvailableDate() {
		return availableDate;
	}

	// TODO Does it make sense for availableDate to be now? Maybe change 'DateHandler.now()' to 10 minutes in the future
	public void setAvailableDate(LocalDateTime availableDate) {
		if (availableDate == null) {
			throw new TutorException(INVALID_AVAILABLE_DATE_FOR_TOURNAMENT);
		}

		if (this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
			throw new TutorException(INVALID_AVAILABLE_DATE_FOR_TOURNAMENT);
		}

		this.availableDate = availableDate;
	}

	public LocalDateTime getConclusionDate() {
		return conclusionDate;
	}

	public void setConclusionDate(LocalDateTime conclusionDate) {
		if (conclusionDate != null && conclusionDate.isBefore(availableDate)) {
			throw new TutorException(INVALID_CONCLUSION_DATE_FOR_TOURNAMENT);
		}

		if (conclusionDate == null) {
			throw new TutorException(INVALID_CONCLUSION_DATE_FOR_TOURNAMENT);
		}

		this.conclusionDate = conclusionDate;
	}

	public LocalDateTime getResultsDate() {
		if (resultsDate == null)
			return conclusionDate;
		return resultsDate;
	}

	public void setResultsDate(LocalDateTime resultsDate) {
		if (resultsDate != null) {
			if (resultsDate.isBefore(availableDate)) {
				throw new TutorException(INVALID_RESULTS_DATE_FOR_TOURNAMENT);
			}

			if (conclusionDate != null && resultsDate.isBefore(conclusionDate)) {
				throw new TutorException(INVALID_RESULTS_DATE_FOR_TOURNAMENT);
			}
		}

		this.resultsDate = resultsDate;
	}

	public Status getStatus() { return this.status; }

	public void setStatus(Status status) {
		if (status == null)
			this.status = Status.ENROLLING;
		else
			this.status = status;
	}

	public void updateStatus() {
		if (status == Status.ONGOING && conclusionDate != null && DateHandler.now().isAfter(conclusionDate))
			setStatus(Status.CONCLUDED);
		else if (status == Status.ENROLLING && availableDate != null && DateHandler.now().isAfter(availableDate))
			setStatus(enrolledUsers.size() >= MINIMUM_ENROLLMENTS ? Status.ONGOING : Status.CONCLUDED);
	}

	public Set<TopicConjunction> getTopicConjunctions() { return topicConjunctions; }

	public void setTopicConjunctions(Set<TopicConjunction> topicConjunctions) {
		this.topicConjunctions = topicConjunctions;
		topicConjunctions.forEach(topicConjunction -> topicConjunction.setTournament(this));
	}

	public void addTopicConjunction(TopicConjunction topicConjunction) {
		this.topicConjunctions.add(topicConjunction);
	}

	public Set<User> getEnrolledUsers() {
		return this.enrolledUsers;
	}

	public void setEnrolledUsers(Set<User> enrolledUsers) {
		this.enrolledUsers = enrolledUsers;
		enrolledUsers.forEach(user -> user.addEnrolledTournament(this));
	}

	public void addEnrolledUser(User user) {
		this.enrolledUsers.add(user);
		user.addEnrolledTournament(this);
	}

	public Set<Question> getQuestions() {
		return this.topicConjunctions.stream()
				.flatMap(topicConjunction -> topicConjunction.getQuestions().stream())
				.collect(Collectors.toSet());
	}

	public Quiz getQuiz() { return this.quiz; }

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
		this.quiz.setTournament(this);
	}

	public void remove() {
		new ArrayList<>(getTopicConjunctions()).forEach(TopicConjunction::remove);
		getTopicConjunctions().clear();

		this.courseExecution.removeTournament(this);
		this.courseExecution = null;
	}

	public boolean canResultsBePublic(int executionId) {
		return true;
	}
}
