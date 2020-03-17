package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.service.performanceTest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.QuestionSuggestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.QuestionSuggestion
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.repository.QuestionSuggestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class AcceptQuestionSuggestionPerformanceTest extends Specification {

    public static final String QUESTION_TITLE = "title"
    public static final String QUESTION_CONTENT = "Is Math Related to Science?"

    @Autowired
    QuestionSuggestionService questionSuggestionService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionSuggestionRepository questionSuggestionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def question

    def setup() {
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.DISABLED)
        questionRepository.save(question)
    }

    def "Performance testing to accept 1000 Question Suggestion"() {
        given: "10000 question suggestion"
        def questionSuggestions = new ArrayList<QuestionSuggestion>()
        for(def i = 0; i < 10000; i++ ) {
            questionSuggestions[i] = new QuestionSuggestion()
            questionSuggestions[i].setQuestion(question)
            questionSuggestions[i].setStatus(QuestionSuggestion.Status.PENDING)
            questionSuggestionRepository.save(questionSuggestions[i])
        }

        when:
        for(def i = 0; i < 10000; i++ ){
        questionSuggestionService.acceptQuestionSuggestion(questionSuggestions[i].getId())
        }

        then: true
    }

    @TestConfiguration
    static class QuestionSuggestionServiceImplTestContextConfiguration {

        @Bean
        QuestionSuggestionService questionSuggestionService() {
            return new QuestionSuggestionService()
        }
    }
}
