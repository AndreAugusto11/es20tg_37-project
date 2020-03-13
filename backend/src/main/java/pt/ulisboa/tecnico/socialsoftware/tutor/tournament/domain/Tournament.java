package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import javax.persistence.*;
import java.time.LocalDateTime;

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

	private Status status;

	public Tournament(User creator)
	{

		if(creator == null) throw new TutorException(ErrorMessage.TOURNAMENT_NON_VALID_USER, creator.getKey());

		users.add(creator);
		this.creator = creator;
		status = Status.OPEN;
	}

	public Tournament(User student, Set<Topic> topics, int number_of_questions, LocalDateTime startTimeArg, LocalDateTime endTimeArg)
	{
		creator = student;
		users.add(creator);
		topics.addAll(topics);
		numQuests = number_of_questions;
		startTime = startTimeArg;
		endTime = endTimeArg;
		if (startTime.isAfter(LocalDateTime.now()))
		{
			status = Status.OPEN;
		}
	}

	public Integer getId()
	{
		return id;
	}

	public User getCreator()
	{
		return creator;
	}


	public Set<User> getUsers()
	{
		return this.users;
	}

	public Set<Topic> getTopics()
	{
		return topics;
	}

	public int getNumQuests()
	{
		return numQuests;
	}

	public LocalDateTime getStartTime()
	{
		return startTime;
	}

	public LocalDateTime getEndTime()
	{
		return endTime;
	}

	public Status getStatus()
	{
		return status;
	}

	private void setCreator(User user)
	{
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

	public void setNumQuests(int num)
	{
		if (num > 0)
		{
			numQuests = num;
		}
		else
		{
			throw(new TutorException(ErrorMessage.TOURNAMENT_INVALID_NUM_QUESTS, num));
		}
	}

	public void setTopics (Set<Topic> topic)
	{
		topics.addAll(topic);
	}

	public void setStartTime(LocalDateTime time)
	{
		startTime = time;
	}

	public void setEndTime(LocalDateTime time)
	{
		endTime = time;
	}

	public void setStatus(Status stat)
	{
		status = stat;
	}

}
