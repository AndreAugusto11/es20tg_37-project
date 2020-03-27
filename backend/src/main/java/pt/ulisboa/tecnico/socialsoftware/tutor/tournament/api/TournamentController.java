package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getTournaments() {
        return tournamentService.getTournaments();
    }

    @PostMapping("/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament(Principal principal, @RequestBody TournamentDto tournamentDto)
    {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.createTournament(user.getId(), tournamentDto);
    }

    @PostMapping("/tournaments/{tournamentId}/enroll")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void enrollTournament(Principal principal, @PathVariable Integer tournamentId)
    {
        User user = (User) ((Authentication) principal).getPrincipal();
        tournamentService.enrollStudentInTournament(user.getId(),tournamentId);
    }
}
