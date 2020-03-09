package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto.QuestionSuggestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuestionSuggestionService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionSuggestionDto createSuggestionQuestion(Integer userId, QuestionSuggestionDto questionSuggestionDto){

        if(userId == null || questionSuggestionDto == null){
            throw new TutorException(INVALID_NULL_ARGUMENTS);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        if(user.getRole() != User.Role.STUDENT){
            throw new TutorException(USER_IS_TEACHER, userId);
        }

        if (questionSuggestionDto.getKey() == null) {
            int maxQuestionNumber = questionSuggestionRepository.getMaxQuestionNumber() != null ?
                    questionSuggestionRepository.getMaxQuestionNumber() : 0;
            questionSuggestionDto.setKey(maxQuestionNumber + 1);
        }

        QuestionSuggestion questionSuggestion = new QuestionSuggestion(user, questionSuggestionDto);
        questionSuggestion.setCreationDate(LocalDateTime.now());
        this.entityManager.persist(questionSuggestion);
        return new QuestionSuggestionDto(questionSuggestion);
    }
}
