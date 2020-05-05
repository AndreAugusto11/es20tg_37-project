package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;

import java.util.List;

@Repository
@Transactional
public interface QuestionSuggestionRepository extends JpaRepository <QuestionSuggestion, Integer> {
    @Query(value = "SELECT * FROM question_suggestions qs WHERE qs.user_id = :userId", nativeQuery = true)
    List<QuestionSuggestion> findQuestionSuggestions(int userId);
}