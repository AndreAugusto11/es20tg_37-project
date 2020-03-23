package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;

import java.util.List;

@Repository
@Transactional
public interface ClarificationRequestRepository extends JpaRepository<ClarificationRequest, Integer> {
    @Query(value = "SELECT * FROM clarification_requests cr JOIN questions q ON q.id = cr.question_id JOIN courses co ON co.id = q.course_id JOIN course_executions ce ON co.id = ce.course_id WHERE ce.id = :executionId", nativeQuery = true)
    List<ClarificationRequest> findClarificationRequestsByCourseExecutions(int executionId);
}
