package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
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

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getEnrolledTournaments(user.getId());
    }

    @GetMapping("/tournaments/created")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TournamentDto> getCreatedTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.getCreatedTournaments(user.getId());
    }

    @PostMapping("/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament(Principal principal, @RequestBody TournamentDto tournamentDto)
    {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.createTournament(user.getId(), tournamentDto);
    }

    @PostMapping("/tournaments/{tournamentId}/enroll")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto enrollTournament(Principal principal, @PathVariable Integer tournamentId)
    {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.enrollStudentInTournament(user.getId(),tournamentId);
    }

    @PostMapping("/tournaments/{tournamentId}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void submitAnswer(Principal principal, @PathVariable int tournamentId, @Valid @RequestBody StatementAnswerDto answer) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.submitAnswer(user.getId(),tournamentId,answer);
    }

    @GetMapping("/tournaments/{tournamentId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public void startQuiz(Principal principal, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        tournamentService.startQuiz(user.getId(),tournamentId);
    }

    @GetMapping("/tournaments/{tournamentId}/conclude")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<CorrectAnswerDto> concludeQuiz(Principal principal, @PathVariable int tournamentId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return tournamentService.concludeQuiz(user.getId(),tournamentId);
    }
}
