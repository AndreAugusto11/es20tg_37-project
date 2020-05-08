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
		OPEN, ONGOING, CLOSED, CANCELLED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToMany
	private Set<User> users = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User creator;

	@OneToOne(cascade = CascadeType.ALL)
	private Quiz quiz;

	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Topic> topics = new HashSet<>();

	@Column(name = "number_of_questions", columnDefinition = "integer default 1")
	private int numQuests = 1;

	@Column(name = "start_date")
	private LocalDateTime startTime;

	@Column(name = "end_date")
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	private Status status = Status.OPEN;

	public Tournament(){}

	public Tournament(User creator)
	{

		if(creator == null) throw new TutorException(ErrorMessage.TOURNAMENT_NON_VALID_USER, creator.getId());

		users.add(creator);
		this.creator = creator;
	}

	public Tournament(User student, Set<Topic> topics, int number_of_questions, LocalDateTime startTimeArg, LocalDateTime endTimeArg)
	{
		creator = student;
		users.add(creator);
		this.topics.addAll(topics);
		numQuests = number_of_questions;
		startTime = startTimeArg;
		endTime = endTimeArg;
		this.checkConsistent();
		if (startTime.isAfter(LocalDateTime.now()))
		{
			status = Status.OPEN;
		}
	}

	public Integer getid()
	{
		return id;
	}

	public User getcreator()
	{
		return creator;
	}

	public Quiz getquiz() {return quiz;}

	public Set<User> getusers()
	{
		return this.users;
	}

	public Set<Topic> gettopics()
	{
		return topics;
	}

	public int getnumQuests()
	{
		return numQuests;
	}

	public LocalDateTime getstartTime()
	{
		return startTime;
	}

	public LocalDateTime getendTime()
	{
		return endTime;
	}

	public Status getstatus()
	{
		return status;
	}

	public void setcreator(User user)
	{
		if(user == null) throw new TutorException(TOURNAMENT_NULL_USER);

		if (user.getRole() == User.Role.STUDENT)
		{
			creator = user;
			users.add(user);
		}
		else
		{
			throw( new TutorException(ErrorMessage.TOURNAMENT_NON_VALID_USER, user.getId()));
		}
	}

	public void addUser(User user)
	{
		users.add(user);
		if (quiz == null) { generateQuiz(); }
		/*else
		{
			QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
			quiz.addQuizAnswer(quizAnswer);
			user.addQuizAnswer(quizAnswer);
		}*/
	}

	public void setnumQuests(Integer num)
	{
		if(num == null) throw new TutorException(TOURNAMENT_NULL_NUM_QUESTS);
		if (num <= 0)
		{
			throw(new TutorException(ErrorMessage.TOURNAMENT_INVALID_NUM_QUESTS, num));
		}
		numQuests = num;
	}

	public void settopics(Set<Topic> topic)
	{
		topics.addAll(topic);
	}

	public void setstartTime(LocalDateTime time)
	{
		if (time == null)
		{
			throw new TutorException(TOURNAMENT_NULL_STARTTIME);
		}
		startTime = time;
	}

	public void setendTime(LocalDateTime time)
	{
		if (time == null)
		{
			throw new TutorException(TOURNAMENT_NULL_ENDTIME);
		}
		endTime = time;
	}

	public void checkConsistent() {
		if (creator.getRole() != User.Role.STUDENT)
		{
			throw new TutorException(TOURNAMENT_NON_VALID_USER, creator.getKey());
		}
		else if (startTime.isBefore(LocalDateTime.now()))
		{
			throw new TutorException(TOURNAMENT_INVALID_STARTTIME);
		}
		else if (startTime.isAfter(endTime) || startTime.isEqual(endTime))
		{
			throw new TutorException(TOURNAMENT_INVALID_TIMEFRAME);
		}
	}

	public void statusUpdate()
	{
		if (startTime == null || endTime == null) { return; }
		if (status == Status.CANCELLED) { return; }
		if (LocalDateTime.now().isAfter(endTime))
		{
			status = Status.CLOSED;
		}
		else if(LocalDateTime.now().isAfter(startTime))
		{
			status = Status.ONGOING;
		}
		else status = Status.OPEN;

	}

	public void setstatus(Status stat)
	{
		status = stat;
	}

	public void setquiz(Quiz quiz) {this.quiz = quiz;}

	public boolean canResultsBePublic(int executionId) {
		return true;
	}

	private void generateQuiz()
	{
		Quiz quiz = new Quiz();

		Random random = new Random();
		int randLimit = topics.size();
		if (randLimit <= 0) { return; }
		List<Question> qst = new ArrayList<>();


		for(int i = 0; i < numQuests; i++)
		{
			int rand = random.nextInt(randLimit);
			int j = 0;

			Iterator<Topic> iterator = topics.iterator();
			while(iterator.hasNext() && j != rand)
			{
				iterator.next();
				j++;
			}

			Topic topic = iterator.next();
			Set<Question> questions = topic.getQuestions();
			int questionidx = random.nextInt(questions.size());
			j = 0;

			Iterator<Question> iter = questions.iterator();
			while (iter.hasNext() && j != questionidx)
			{
				iter.next();
				j++;
			}
			qst.add(iter.next());
		}

		quiz.setAvailableDate(startTime);
		quiz.setCreationDate(LocalDateTime.now());
		quiz.setConclusionDate(endTime);
		quiz.setResultsDate(endTime);


		IntStream.range(0,qst.size())
				.forEach(index -> quiz.addQuizQuestion(new QuizQuestion(quiz, qst.get(index), index)));

		quiz.setType(Quiz.QuizType.PROPOSED.toString());
		quiz.setTitle("Generated Quiz");

		for (User u : users)
		{
			QuizAnswer quizAnswer = new QuizAnswer(u, quiz);
			quiz.addQuizAnswer(quizAnswer);

			//Line commented to avoid psql BD errors
			//u.addQuizAnswer(quizAnswer);
		}

		CourseExecution courseExecution = topics.iterator().next().getCourse().getCourseExecutions().iterator().next();
		quiz.setCourseExecution(courseExecution);
		courseExecution.addQuiz(quiz);



		quiz.setKey(quiz.getId());
		setquiz(quiz);
	}
}
