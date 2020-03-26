package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.function.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

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
		tournament.setcreator(student);
		if(userRepository.findByKey(student.getKey()) == null) throw new TutorException(TOURNAMENT_NON_VALID_USER);

		if (topics == null) throw new TutorException(TOURNAMENT_NULL_TOPIC);
		if(!this.checkTopicsExistence(topics)) throw new TutorException(TOURNAMENT_INVALID_TOPIC);
		tournament.settopics(topics);

		tournament.setnumQuests(numOfQuestions);
		tournament.setstartTime(startTime);
		tournament.setendTime(endTime);

		tournament.checkConsistent();

		this.entityManager.persist(tournament);
		return new TournamentDto(tournament);
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public TournamentDto createTournament(Integer userId, TournamentDto tournamentDto)
	{
		User student = userRepository.findById(userId).get();
		Set<Integer> tmp = tournamentDto.gettopics();
		Set<Topic> topics = new HashSet<>();
		for(Integer id: tmp){
			topics.add(topicRepository.findById(id).get());
		}
		Integer numOfQuestions = tournamentDto.getnumQuests();
		LocalDateTime start = tournamentDto.getstartTime();
		LocalDateTime end = tournamentDto.getendTime();
		return createTournament(student, topics, numOfQuestions, start, end);
	}

	public TournamentDto findTournamentById(Integer id){
		return tournamentRepository.findById(id).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,id));
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public void enrollStudentInTournament(Integer userId, Integer tournamentId){
		if(userId == null) throw new TutorException(TOURNAMENT_NULL_USER);

		if(tournamentId == null) throw new TutorException(TOURNAMENT_NULL_TOURNAMENT);

		Tournament tournament = tournamentRepository.findById(tournamentId)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND,tournamentId));
		User user = userRepository.findById(userId)
				.orElseThrow( () -> new TutorException(USER_NOT_FOUND,userId));

		if(user.getRole() != User.Role.STUDENT) throw new TutorException(TOURNAMENT_NOT_STUDENT);

		if(tournament.getstatus() != Tournament.Status.OPEN) throw new TutorException(TOURNAMENT_NOT_OPEN,tournamentId);

		Predicate<User> u1 = s -> s.getId().equals(userId);

		if(tournament.getusers().stream().anyMatch(u1)) throw new TutorException(TOURNAMENT_STUDENT_ALREADY_ENROLLED,userId);

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

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public List<TournamentDto> getTournaments() {
		return tournamentRepository.findAll().stream()
				.map(TournamentDto::new)
				.sorted(Comparator
						.comparing(TournamentDto::getid))
				.collect(Collectors.toList());
	}
}