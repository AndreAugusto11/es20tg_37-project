package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import java.util.*;
import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

	public enum Status {
		OPEN, ONGOING, CLOSED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToMany(cascade = CascadeType.ALL)
	private Set<User> users = new HashSet<>();

	@ManyToOne(cascade = CascadeType.ALL)
	private User creator;

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
		topics.addAll(topics);
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
		}
		else
		{
			throw( new TutorException(ErrorMessage.TOURNAMENT_NON_VALID_USER, user.getId()));
		}
	}

	public void addUser(User user)
	{
		users.add(user);
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

	public void setstatus(Status stat)
	{
		status = stat;
	}


	public void deleteQuiz() {
	}

}
