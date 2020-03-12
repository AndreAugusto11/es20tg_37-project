package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND;

@Service
public class TournamentService
{
	@Autowired
	private TournamentRepository tournamentRepository;
	
	public TournamentDto createTournament(User student, TopicDto topic, int num_of_questions, LocalDateTime startTime, LocalDateTime endTime)
	{}

	public TournamentDto createTournament(User student, TopicDto[] topics, int num_of_questions, LocalDateTime startTime, LocalDateTime endTime)
	{}

	public TournamentDto findTournamentByKey(Integer key){
		return tournamentRepository.findByKey(key).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,key));
	}

	public void enrollStudentInTournament(Integer userKey, Integer tournamentKey){
		TournamentDto tournament = tournamentRepository.findByKey(tournamentKey)
				.orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND,tournamentKey));
		tournament.enroll(userKey);
	}
}