package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification;

import java.util.Optional;

@Repository
@Transactional
public interface JustificationRepository extends JpaRepository <Justification, Integer> {
    @Query(value = "SELECT * FROM justifications j WHERE j.key = :key", nativeQuery = true)
    Optional<Justification> findByKey(Integer key);
}
