package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification;

@Repository
@Transactional
public interface JustificationRepository extends JpaRepository <Justification, Integer>{
}
