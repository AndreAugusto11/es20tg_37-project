package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND;

public class TournamentService
{
	private TournamentRepository tournamentRepository;

	public void createTournament()
	{

	}

	public TournamentDto findTournamentByKey(Integer key){
		return tournamentRepository.findByKey(key).map(TournamentDto::new)
				.orElseThrow( () -> new TutorException(TOURNAMENT_NOT_FOUND,key));
	}
}