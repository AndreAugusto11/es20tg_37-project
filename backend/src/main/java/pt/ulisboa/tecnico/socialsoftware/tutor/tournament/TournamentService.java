package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import java.util.*;
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

	public TournamentDto createTournament(Integer userKey)
	{
		User user = userRepository.findByKey(userKey);
		return new TournamentDto(new Tournament(user));
	}

	public TournamentDto createTournament(User student, Set<Topic> topics, int num_of_questions, LocalDateTime startTime, LocalDateTime endTime)
	{
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
}