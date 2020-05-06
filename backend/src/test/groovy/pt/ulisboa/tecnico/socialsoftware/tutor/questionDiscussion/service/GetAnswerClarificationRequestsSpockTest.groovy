package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.QuestionDiscussionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.PublicClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.repository.PublicClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Shared;
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;

@DataJpaTest
class GetAnswerClarificationRequestsSpockTest extends Specification {
    public static final String USERNAME_TEACHER = "username_teacher"
    public static final String USERNAME_STUDENT = "username_student"
    public static final String USERNAME_STUDENT_1 = "username_student_1"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String COURSE_NAME2 = "Software Engineering"
    public static final String CLARIFICATION_CONTENT_1 = "clarification request content 1"
    public static final String CLARIFICATION_CONTENT_2 = "clarification request content 2"
    public static final String ACRONYM = "AS1"
    public static final String ACRONYM1 = "AS2"
    public static final String ACRONYM2 = "AS3"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TIME_TAKEN = 1234
    public static final String QUIZ_TITLE = 'quiz title'
    public static final String QUESTION_TITLE = 'question title'
    public static final String QUESTION_CONTENT = 'question content'
    public static final String OPTION_CONTENT = "optionId content"
    public static final Integer SEQUENCE = 0
    public static final String URL = 'URL'
    private Random rand = new Random();

    @Autowired
    QuestionDiscussionService questionDiscussionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    PublicClarificationRequestRepository publicClarificationRequestRepository

    @Shared
    def user_teacher
    def user_student
    def user_student_1
    def course
    def course2
    def courseExecution
    def courseExecution2
    def courseExecution3
    def question1
    def question2
    def quizQuestion
    def option
    def quizAnswer1
    def quizAnswer2
    def quiz
    def questionAnswer1
    def questionAnswer2

    def setup() {

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course2 = new Course(COURSE_NAME2, Course.Type.TECNICO)
        courseRepository.save(course)
        courseRepository.save(course2)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution2 = new CourseExecution(course, ACRONYM1, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution3 = new CourseExecution(course2, ACRONYM2, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        courseExecutionRepository.save(courseExecution2)
        courseExecutionRepository.save(courseExecution3)

        user_teacher = new User("name", USERNAME_TEACHER, 1, User.Role.TEACHER)
        user_student = new User("name", USERNAME_STUDENT, 2, User.Role.STUDENT)
        user_student_1 = new User("name", USERNAME_STUDENT_1, 3, User.Role.STUDENT)
        user_teacher.getCourseExecutions().add(courseExecution)
        user_student.getCourseExecutions().add(courseExecution)
        user_student_1.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user_teacher)
        courseExecution.getUsers().add(user_student)
        courseExecution.getUsers().add(user_student_1)
        userRepository.save(user_teacher)
        userRepository.save(user_student)
        userRepository.save(user_student_1)

        question1 = new Question()
        question1.setCourse(course)
        course.addQuestion(question1)
        question1.setKey(1)
        question1.setTitle(QUESTION_TITLE)
        question1.setContent(QUESTION_CONTENT)
        question1.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question1)

        question2 = new Question()
        question2.setCourse(course)
        course.addQuestion(question2)
        question2.setKey(1)
        question2.setTitle(QUESTION_TITLE)
        question2.setContent(QUESTION_CONTENT)
        question2.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question2)

        option = new Option()
        option.setSequence(SEQUENCE)
        option.setContent(OPTION_CONTENT)
        option.setCorrect(true)
        option.setQuestion(question1)
        option.setQuestion(question2)
        question1.addOption(option)
        question2.addOption(option)
        optionRepository.save(option)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED.name())
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)
        quizRepository.save(quiz)

        quizQuestion = new QuizQuestion(quiz, question1, SEQUENCE)
        quizQuestion = new QuizQuestion(quiz, question2, SEQUENCE)
        quiz.addQuizQuestion(quizQuestion)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer1 = new QuizAnswer(user_student, quiz)
        quizAnswer2 = new QuizAnswer(user_student_1, quiz)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswerRepository.save(quizAnswer2)
    }

    def "get public clarification requests associated to a question when there are no more clarification requests associated with the question"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request of each student"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer2, question1, user_student_1, CLARIFICATION_CONTENT_2)

        and: "make the clarification requests public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        PublicClarificationRequest publicClarificationRequest2 = new PublicClarificationRequest(course, clarificationRequest2)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        clarificationRequest2.setPublicClarificationRequest(publicClarificationRequest2)
        course.addPublicClarificationRequests(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest2)

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(courseExecution.getId(), question1.getId())

        then: "the public clarification request is not in the repository"
        list.size() == 2
        ClarificationRequestDto clarificationRequestDto1 = new ClarificationRequestDto(clarificationRequest1);
        ClarificationRequestDto clarificationRequestDto2 = new ClarificationRequestDto(clarificationRequest2);

        list.get(0).getId() == clarificationRequestDto1.getId()
        list.get(0).getContent() == clarificationRequestDto1.getContent()
        list.get(0).getName() == clarificationRequestDto1.getName()
        list.get(0).getStatus() == clarificationRequestDto1.getStatus()

        list.get(1).getId() == clarificationRequestDto2.getId()
        list.get(1).getContent() == clarificationRequestDto2.getContent()
        list.get(1).getName() == clarificationRequestDto2.getName()
        list.get(1).getStatus() == clarificationRequestDto2.getStatus()
    }

    def "get public clarification request associated to a question when there are more clarification requests associated with the question"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request of each student"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer2, question1, user_student_1, CLARIFICATION_CONTENT_2)

        and: "make the clarification request public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest1)

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(courseExecution.getId(), question1.getId())

        then: "the public clarification request is not in the repository"
        list.size() == 1
        ClarificationRequestDto clarificationRequestDto1 = new ClarificationRequestDto(clarificationRequest1);

        list.get(0).getId() == clarificationRequestDto1.getId()
        list.get(0).getContent() == clarificationRequestDto1.getContent()
        list.get(0).getName() == clarificationRequestDto1.getName()
        list.get(0).getStatus() == clarificationRequestDto1.getStatus()
    }

    def "get the only public clarification requests associated to a question when there are mora clarification requests but associated to another question"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request of each student"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer2, question2, user_student_1, CLARIFICATION_CONTENT_2)

        and: "make the clarification requests public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        PublicClarificationRequest publicClarificationRequest2 = new PublicClarificationRequest(course, clarificationRequest2)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        clarificationRequest2.setPublicClarificationRequest(publicClarificationRequest2)
        course.addPublicClarificationRequests(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest2)

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(courseExecution.getId(), question1.getId())

        then: "the public clarification request is not in the repository"
        list.size() == 1
        ClarificationRequestDto clarificationRequestDto1 = new ClarificationRequestDto(clarificationRequest1);

        list.get(0).getId() == clarificationRequestDto1.getId()
        list.get(0).getContent() == clarificationRequestDto1.getContent()
        list.get(0).getName() == clarificationRequestDto1.getName()
        list.get(0).getStatus() == clarificationRequestDto1.getStatus()
    }

    def "get public clarification requests associated to a non existing course execution"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)

        and: "make the clarification request public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest1)

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(rand.nextInt(1000000), question1.getId())

        then: "the public clarification request is not in the repository"
        def error = thrown(TutorException)
        error.errorMessage == COURSE_EXECUTION_NOT_FOUND
    }

    def "get public clarification requests associated to another course execution of the same course"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request of each student"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)
        def clarificationRequest2 = new ClarificationRequest(questionAnswer2, question1, user_student_1, CLARIFICATION_CONTENT_2)

        and: "make the clarification requests public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        PublicClarificationRequest publicClarificationRequest2 = new PublicClarificationRequest(course, clarificationRequest2)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        clarificationRequest2.setPublicClarificationRequest(publicClarificationRequest2)
        course.addPublicClarificationRequests(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest2)

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(courseExecution2.getId(), question1.getId())

        then: "the public clarification request is not in the repository"
        list.size() == 2
        ClarificationRequestDto clarificationRequestDto1 = new ClarificationRequestDto(clarificationRequest1);
        ClarificationRequestDto clarificationRequestDto2 = new ClarificationRequestDto(clarificationRequest2);

        list.get(0).getId() == clarificationRequestDto1.getId()
        list.get(0).getContent() == clarificationRequestDto1.getContent()
        list.get(0).getName() == clarificationRequestDto1.getName()
        list.get(0).getStatus() == clarificationRequestDto1.getStatus()

        list.get(1).getId() == clarificationRequestDto2.getId()
        list.get(1).getContent() == clarificationRequestDto2.getContent()
        list.get(1).getName() == clarificationRequestDto2.getName()
        list.get(1).getStatus() == clarificationRequestDto2.getStatus()
    }

    def "get public clarification requests associated to another course execution of another course"() {
        given: "a question answer answered"
        questionAnswer1 = new QuestionAnswer(quizAnswer1, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        questionAnswer2 = new QuestionAnswer(quizAnswer2, quizQuestion, TIME_TAKEN, option, SEQUENCE)
        quizAnswer1.addQuestionAnswer(questionAnswer1)
        quizAnswer2.addQuestionAnswer(questionAnswer2)
        questionAnswerRepository.save(questionAnswer1)
        questionAnswerRepository.save(questionAnswer2)

        and: "one clarification request"
        def clarificationRequest1 = new ClarificationRequest(questionAnswer1, question1, user_student, CLARIFICATION_CONTENT_1)

        and: "make the clarification request public"
        PublicClarificationRequest publicClarificationRequest1 = new PublicClarificationRequest(course, clarificationRequest1)
        clarificationRequest1.setPublicClarificationRequest(publicClarificationRequest1)
        course.addPublicClarificationRequests(publicClarificationRequest1)

        println courseExecution3.getId()

        when:
        def list = questionDiscussionService.getAnswerClarificationRequests(courseExecution3.getId(), question1.getId())

        then: "the public clarification request is not in the repository"
        list.size() == 0
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionDiscussionService questionDiscussionService() {
            return new QuestionDiscussionService()
        }

    }
}