package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

	@PersistenceContext
	EntityManager entityManager;

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(User student, Set<Topic> topics, Integer numOfQuestions, LocalDateTime startTime, LocalDateTime endTime)
	{
		Tournament tournament = new Tournament();
		tournament.setCreator(student);
		if(userRepository.findByKey(student.getKey()) == null) throw new TutorException(TOURNAMENT_NON_VALID_USER);

		if (topics == null) throw new TutorException(TOURNAMENT_NULL_TOPIC);
		if(!this.checkTopicsExistence(topics)) throw new TutorException(TOURNAMENT_INVALID_TOPIC);
		tournament.setTopics(topics);

		tournament.setNumQuests(numOfQuestions);
		tournament.setStartTime(startTime);
		tournament.setEndTime(endTime);

		tournament.checkConsistent();

		this.entityManager.persist(tournament);
		return new TournamentDto(tournament);
	}

	public TournamentDto findTournamentById(Integer id){
		return tournamentRepository.findById(id).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,id));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
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