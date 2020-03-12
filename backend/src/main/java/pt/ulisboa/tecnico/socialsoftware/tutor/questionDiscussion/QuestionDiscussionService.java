package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequestAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.sql.SQLException;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

public class QuestionDiscussionService {

    @Autowired
    QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository;

    @Autowired
    ClarificationRequestAnswerRepository clarificationRequestAnswerRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto createClarificationRequest(Integer questionAnswerId, ClarificationRequestDto clarificationRequestDto) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));

        Question question = questionRepository
                .findById(clarificationRequestDto.getQuestionAnswer().getQuestion().getId())
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND,
                        clarificationRequestDto.getQuestionAnswer().getQuestion().getId()));

        User user = userRepository.findByUsername(clarificationRequestDto.getUsername());
        if (user == null) {
            throw new TutorException(USER_NOT_FOUND_USERNAME, clarificationRequestDto.getUsername());
        }

        String content = clarificationRequestDto.getContent();

        if (user != questionAnswer.getQuizAnswer().getUser()) {
            throw new TutorException(QUESTION_ANSWER_MISMATCH_USER, String.valueOf(questionAnswer.getId()), user.getUsername());
        }

        if (question != questionAnswer.getQuizQuestion().getQuestion()) {
            throw new TutorException(QUESTION_ANSWER_MISMATCH_QUESTION, String.valueOf(questionAnswer.getId()), String.valueOf(question.getId()));
        }

        ClarificationRequest clarificationRequest = new ClarificationRequest(questionAnswer, question, user, content);
        if (clarificationRequestDto.getImage() != null) {
            Image img = new Image(clarificationRequestDto.getImage());
            clarificationRequest.setImage(img);
            img.setClarificationRequest(clarificationRequest);
        }

        entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    public ClarificationRequestAnswerDto createClarificationRequestAnswer(ClarificationRequestAnswerDto clarificationRequestAnswerDto) {

        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationRequestAnswerDto.getClarificationRequest().getId())
                .orElseThrow(() -> new TutorException(CLARIFICATION_REQUEST_NOT_FOUND, clarificationRequestAnswerDto.getClarificationRequest().getId()));

        User user = userRepository.findByUsername(clarificationRequestAnswerDto.getUsername());
        if (user == null) {
            throw new TutorException(USER_NOT_FOUND_USERNAME, clarificationRequestAnswerDto.getUsername());
        }

        ClarificationRequestAnswer clarificationRequestAnswer = new ClarificationRequestAnswer(clarificationRequest, user, clarificationRequestAnswerDto.getContent());

        entityManager.persist(clarificationRequestAnswer);
        return new ClarificationRequestAnswerDto(clarificationRequestAnswer);
    }
}
