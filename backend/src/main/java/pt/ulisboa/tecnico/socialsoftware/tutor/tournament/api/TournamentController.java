package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

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

    @GetMapping("/tournaments/enrolled")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getEnrolledTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.getEnrolledTournaments(user.getId());
    }

    @GetMapping("/tournaments/created")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getCreatedTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.getCreatedTournaments(user.getId());
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
    public TournamentDto enrollTournament(Principal principal, @PathVariable Integer tournamentId)
    {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.enrollStudentInTournament(user.getId(),tournamentId);
    }

    @GetMapping("/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto cancelTournament(Principal principal, @PathVariable Integer tournamentId)
    {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        Integer userId = user.getId();
        System.out.println("iniciating with uid = " + userId.toString() + " and tid = " + tournamentId.toString());

        return tournamentService.cancelTournament(userId, tournamentId);
    }
}
