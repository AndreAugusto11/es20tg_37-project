package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
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
        QuestionAnswer questionAnswer = getQuestionAnswer(questionAnswerId);

        Question question = getQuestion(clarificationRequestDto);

        User user = getUser(clarificationRequestDto.getUsername());

        checkQuestionAnswerMatchUser(questionAnswer, user);

        checkQuestionAnswerMatchQuestion(questionAnswer, question);

        ClarificationRequest clarificationRequest = new ClarificationRequest(questionAnswer, question, user, clarificationRequestDto.getContent());
        if (clarificationRequestDto.getImage() != null) {
            addImage(clarificationRequestDto, clarificationRequest);
        }

        entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    private Question getQuestion(ClarificationRequestDto clarificationRequestDto) {
        return questionRepository
                    .findById(clarificationRequestDto.getQuestionAnswer().getQuestion().getId())
                    .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, clarificationRequestDto.getQuestionAnswer().getQuestion().getId()));
    }

    private QuestionAnswer getQuestionAnswer(Integer questionAnswerId) {
        if (questionAnswerId == null) {
            throw new TutorException(QUESTION_ANSWER_NOT_DEFINED);
        }
        return questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswerId));
    }

    private User getUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new TutorException(USER_NOT_FOUND_USERNAME, username);
        }
        return user;
    }

    private void addImage(ClarificationRequestDto clarificationRequestDto, ClarificationRequest clarificationRequest) {
        Image img = new Image(clarificationRequestDto.getImage());
        clarificationRequest.setImage(img);
        img.setClarificationRequest(clarificationRequest);
    }

    private void checkQuestionAnswerMatchQuestion(QuestionAnswer questionAnswer, Question question) {
        if (question != questionAnswer.getQuizQuestion().getQuestion()) {
            throw new TutorException(QUESTION_ANSWER_MISMATCH_QUESTION, String.valueOf(questionAnswer.getId()), String.valueOf(question.getId()));
        }
    }

    private void checkQuestionAnswerMatchUser(QuestionAnswer questionAnswer, User user) {
        if (user != questionAnswer.getQuizAnswer().getUser()) {
            throw new TutorException(QUESTION_ANSWER_MISMATCH_USER, String.valueOf(questionAnswer.getId()), user.getUsername());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestAnswerDto createClarificationRequestAnswer(ClarificationRequestAnswerDto clarificationRequestAnswerDto) {

        checkClarificationRequest(clarificationRequestAnswerDto);

        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationRequestAnswerDto.getClarificationRequest().getId())
                .orElseThrow(() -> new TutorException(CLARIFICATION_REQUEST_NOT_FOUND, clarificationRequestAnswerDto.getClarificationRequest().getId()));

        User user = getUser(clarificationRequestAnswerDto.getUsername());

        checkClarificationRequestAnswerType(clarificationRequestAnswerDto);

        ClarificationRequestAnswer clarificationRequestAnswer = new ClarificationRequestAnswer(clarificationRequest, clarificationRequestAnswerDto.getType(), user, clarificationRequestAnswerDto.getContent());

        entityManager.persist(clarificationRequestAnswer);
        return new ClarificationRequestAnswerDto(clarificationRequestAnswer);
    }

    private void checkClarificationRequestAnswerType(ClarificationRequestAnswerDto clarificationRequestAnswerDto) {
        if (clarificationRequestAnswerDto.getType() == null) {
            throw new TutorException(CLARIFICATION_REQUEST_ANSWER_TYPE_NOT_DEFINED);
        }
    }

    private void checkClarificationRequest(ClarificationRequestAnswerDto clarificationRequestAnswerDto) {
        if (clarificationRequestAnswerDto.getClarificationRequest() == null) {
            throw new TutorException(CLARIFICATION_REQUEST_NOT_DEFINED);
        }
    }
}