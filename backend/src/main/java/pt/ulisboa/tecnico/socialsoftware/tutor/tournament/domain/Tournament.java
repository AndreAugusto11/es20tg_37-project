package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {
	public enum Status {
		CREATED, ONGOING, CLOSED, CANCELLED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToMany
	private final Set<User> users = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User creator;

	@OneToOne(cascade = CascadeType.ALL)
	private Quiz quiz;

	@ManyToMany(cascade = CascadeType.ALL)
	private final Set<Topic> topics = new HashSet<>();

	@Column(name = "number_of_questions", columnDefinition = "integer default 1")
	private int numberQuestions = 1;

	@Column(name = "start_date")
	private LocalDateTime startTime;

	@Column(name = "end_date")
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	private Status status = Status.CREATED;

	public Tournament(){}

	public Tournament(User student, Set<Topic> topics, int numberQuestions, LocalDateTime startTime, LocalDateTime endTime) {
		this.setCreator(student);
		this.setTopics(topics);
		this.numberQuestions = numberQuestions;
		this.setTimeframe(startTime, endTime);
	}

	public Integer getId() {
		return this.id;
	}

	public User getCreator() {
		return this.creator;
	}

	public Quiz getQuiz() {return quiz;}

	public Set<User> getUsers() {
		return this.users;
	}

	public Set<Topic> getTopics() {
		return this.topics;
	}

	public int getNumberQuestions() {
		return this.numberQuestions;
	}

	public LocalDateTime getStartTime() {
		return this.startTime;
	}

	public LocalDateTime getEndTime() {
		return this.endTime;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setCreator(User user) {
		if (user == null) 
			throw new TutorException(TOURNAMENT_NULL_USER);

		if (user.getRole() == User.Role.STUDENT) {
			this.creator = user;
			this.users.add(user);
		}
		else {
			throw new TutorException(ErrorMessage.TOURNAMENT_NON_VALID_USER, user.getId());
		}
	}

	public void addUser(User user) {
		this.users.add(user);
		if (this.quiz == null) { generateQuiz(); }
		/*else
		{
			QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
			quiz.addQuizAnswer(quizAnswer);
			user.addQuizAnswer(quizAnswer);
		}*/
	}

	public void setNumberQuestions(Integer num) {
		if (num == null)
			throw new TutorException(TOURNAMENT_NULL_NUM_QUESTS);

		if (num <= 0) {
			throw new TutorException(ErrorMessage.TOURNAMENT_INVALID_NUM_QUESTS, num);
		}
		this.numberQuestions = num;
	}

	public void setTopics(Set<Topic> topics) {
		if (topics == null)
			throw new TutorException(TOURNAMENT_NULL_TOPIC);
		this.topics.addAll(topics);
	}

	public void setStartTime(LocalDateTime time) {
		if (time == null)
			throw new TutorException(TOURNAMENT_NULL_STARTTIME);

		this.startTime = time;
	}

	public void setEndTime(LocalDateTime time) {
		if (time == null)
			throw new TutorException(TOURNAMENT_NULL_ENDTIME);

		this.endTime = time;
	}

	public void setTimeframe(LocalDateTime startTime, LocalDateTime endTime) {
		if (startTime == null)
			throw new TutorException(TOURNAMENT_NULL_STARTTIME);

		if (endTime == null)
			throw new TutorException(TOURNAMENT_NULL_ENDTIME);

		if (startTime.isBefore(LocalDateTime.now())) {
			throw new TutorException(TOURNAMENT_INVALID_STARTTIME);
		}

		else if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
			throw new TutorException(TOURNAMENT_INVALID_TIMEFRAME);
		}

		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void statusUpdate() {
		if (this.startTime != null && this.endTime != null && this.status != Status.CANCELLED) {
			if (LocalDateTime.now().isAfter(endTime)) {
				this.status = Status.CLOSED;
			}
			else if (LocalDateTime.now().isAfter(startTime)) {
				this.status = Status.ONGOING;
			}
			else this.status = Status.CREATED;
		}
	}

	public void setStatus(Status stat) {
		this.status = stat;
	}

	public void setQuiz(Quiz quiz) { this.quiz = quiz; }

	public boolean canResultsBePublic(int executionId) {
		return true;
	}

	private void generateQuiz() {
		Quiz quiz = new Quiz();

		Random random = new Random();
		int randLimit = this.topics.size();
		if (randLimit <= 0) {
			return;
		}
		List<Question> qst = new ArrayList<>();


		for (int i = 0; i < this.numberQuestions; i++) {
			int rand = random.nextInt(randLimit);
			int j = 0;

			Iterator<Topic> iterator = this.topics.iterator();
			while(iterator.hasNext() && j != rand) {
				iterator.next();
				j++;
			}

			Topic topic = iterator.next();
			Set<Question> questions = topic.getQuestions();
			int questionidx = random.nextInt(questions.size());
			j = 0;

			Iterator<Question> iter = questions.iterator();
			while (iter.hasNext() && j != questionidx) {
				iter.next();
				j++;
			}
			qst.add(iter.next());
		}

		quiz.setAvailableDate(this.startTime);
		quiz.setCreationDate(LocalDateTime.now());
		quiz.setConclusionDate(this.endTime);
		quiz.setResultsDate(this.endTime);


		IntStream.range(0,qst.size())
				.forEach(index -> quiz.addQuizQuestion(new QuizQuestion(quiz, qst.get(index), index)));

		quiz.setType(Quiz.QuizType.PROPOSED.toString());
		quiz.setTitle("Generated Quiz");

		for (User u : this.users) {
			QuizAnswer quizAnswer = new QuizAnswer(u, quiz);
			quiz.addQuizAnswer(quizAnswer);

			//Line commented to avoid psql BD errors
			//u.addQuizAnswer(quizAnswer);
		}

		CourseExecution courseExecution = this.topics.iterator().next().getCourse().getCourseExecutions().iterator().next();
		quiz.setCourseExecution(courseExecution);
		courseExecution.addQuiz(quiz);

		quiz.setKey(quiz.getId());
		setQuiz(quiz);
	}
}
