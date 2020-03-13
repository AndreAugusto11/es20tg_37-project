package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService
{
	@Autowired
	private TournamentRepository tournamentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TopicRepository topicRepository;

	public TournamentDto createTournament(Integer userKey)
	{
		User user = userRepository.findByKey(userKey);
		return new TournamentDto(new Tournament(user));
	}

	public TournamentDto createTournament(User student, Set<Topic> topics, Integer num_of_questions, LocalDateTime startTime, LocalDateTime endTime)
	{
		if (student == null)
		{
			throw new TutorException(TOURNAMENT_NULL_USER);
		}
		else if (topics == null)
		{
			throw new TutorException(TOURNAMENT_NULL_TOPIC);
		}
		else if (num_of_questions == null)
		{
			throw new TutorException(TOURNAMENT_NULL_NUM_QUESTS);
		}
		else if (startTime == null)
		{
			throw new TutorException(TOURNAMENT_NULL_STARTTIME);
		}
		else if (endTime == null)
		{
			throw new TutorException(TOURNAMENT_NULL_ENDTIME);
		}
		else if (student.getRole() != User.Role.STUDENT)
		{
			throw new TutorException(TOURNAMENT_NON_VALID_USER, student.getKey());
		}
		else if (!checkTopicsExistence(topics))
		{
			throw new TutorException(TOURNAMENT_INVALID_TOPIC);
		}
		else if (num_of_questions <= 0)
		{
			throw new TutorException(TOURNAMENT_INVALID_NUM_QUESTS);
		}
		else if (startTime.isBefore(LocalDateTime.now()))
		{
			throw new TutorException(TOURNAMENT_INVALID_STARTTIME);
		}
		else if (startTime.isAfter(endTime) || startTime.isEqual(endTime))
		{
			throw new TutorException(TOURNAMENT_INVALID_TIMEFRAME);
		}
		Tournament tournament = new Tournament(student, topics, num_of_questions, startTime, endTime);
		return new TournamentDto(tournament);
	}

	public TournamentDto convertTournament(Tournament tournament)
	{
		return new TournamentDto(tournament);
	}

	public TournamentDto findTournamentById(Integer id){
		return tournamentRepository.findById(id).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,id));
	}

	public void enrollStudentInTournament(Integer userKey, Integer tournamentId){
		if(userKey == null) throw new TutorException(TOURNAMENT_NULL_USER);

		if(tournamentId == null) throw new TutorException(TOURNAMENT_NULL_TOURNAMENT);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND,tournamentId));
		User user = userRepository.findByKey(userKey);

		if(user.getRole() != User.Role.STUDENT) throw new TutorException(TOURNAMENT_NOT_STUDENT);

		if(tournament.getStatus() != Tournament.Status.OPEN) throw new TutorException(TOURNAMENT_NOT_OPEN,tournamentId);

		Predicate<User> u1 = s -> s.getKey().equals(userKey);

		if(tournament.getUsers().stream().anyMatch(u1)) throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED,userKey);

		tournament.addUser(user);
		user.addTournament(tournament);
	}

	private boolean checkTopicsExistence(Set<Topic> topics)
	{
		Iterator<Topic> iter = topics.iterator();
		while(iter.hasNext())
		{
			Topic t1 = iter.next();
			Topic topic = topicRepository.findTopicByName(t1.getCourse().getId(), t1.getName());
			if (topic == null)
			{
				return false;
			}
		}
		return true;
	}
}