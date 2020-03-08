package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;

@Repository
@Transactional
public interface ClarificationRequestRepository extends JpaRepository<ClarificationRequest, Integer> {
}
