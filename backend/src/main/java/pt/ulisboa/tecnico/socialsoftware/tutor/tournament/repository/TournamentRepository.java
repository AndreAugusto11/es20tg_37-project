package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "SELECT * FROM tournaments t", nativeQuery = true)
    List<Tournament> findTournaments();

    @Query(value = "SELECT * FROM tournaments t WHERE t.id = :id", nativeQuery = true)
    Optional<Tournament> findById(Integer id);
}
