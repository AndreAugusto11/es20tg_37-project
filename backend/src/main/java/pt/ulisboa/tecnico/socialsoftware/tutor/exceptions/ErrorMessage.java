package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {

    INVALID_ACADEMIC_TERM_FOR_COURSE_EXECUTION("Invalid academic term for course execution"),
    INVALID_ACRONYM_FOR_COURSE_EXECUTION("Invalid acronym for course execution"),
    INVALID_CONTENT_FOR_OPTION("Invalid content for option"),
    INVALID_CONTENT_FOR_QUESTION("Invalid content for question"),
    INVALID_NAME_FOR_COURSE("Invalid name for course"),
    INVALID_NAME_FOR_TOPIC("Invalid name for topic"),
    INVALID_SEQUENCE_FOR_OPTION("Invalid sequence for option"),
    INVALID_SEQUENCE_FOR_QUESTION_ANSWER("Invalid sequence for question answer"),
    INVALID_TITLE_FOR_ASSESSMENT("Invalid title for assessment"),
    INVALID_TITLE_FOR_QUESTION("Invalid title for question"),
    INVALID_URL_FOR_IMAGE("Invalid url for image"),
    INVALID_TYPE_FOR_COURSE("Invalid type for course"),
    INVALID_TYPE_FOR_COURSE_EXECUTION("Invalid type for course execution"),
    INVALID_AVAILABLE_DATE_FOR_QUIZ("Invalid available date for quiz"),
    INVALID_CONCLUSION_DATE_FOR_QUIZ("Invalid conclusion date for quiz"),
    INVALID_RESULTS_DATE_FOR_QUIZ("Invalid results date for quiz"),
    INVALID_TITLE_FOR_QUIZ("Invalid title for quiz"),
    INVALID_TYPE_FOR_QUIZ("Invalid type for quiz"),
    INVALID_QUESTION_SEQUENCE_FOR_QUIZ("Invalid question sequence for quiz"),

    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),

    USER_NOT_FOUND_USERNAME("User not found with id %s"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    
    TOURNAMENT_NOT_FOUND("Tournament not found with id %d"),
    TOURNAMENT_NOT_OPEN("Tournament with id %d not open"),
    TOURNAMENT_NOT_STUDENT("Tournament only allows students to enroll"),
    TOURNAMENT_STUDENT_ALREADY_ENROLLED("Tournament already enrolled student with id %d"),
    TOURNAMENT_NON_CREATOR("Tournament %d was not created by User with id %d"),

    TOURNAMENT_NULL_ID("Tournament ID is null"),
    TOURNAMENT_NULL_USER("User is null"),
    TOURNAMENT_NULL_TOURNAMENT("Tournament is null"),
    TOURNAMENT_NULL_TOPIC("Topic is null in Tournament Creation"),
    TOURNAMENT_NULL_NUM_QUESTS("Number of Questions is null in Tournament Creation"),
    TOURNAMENT_NULL_STARTTIME("Start Time is null in Tournament Creation"),
    TOURNAMENT_NULL_ENDTIME("End Time is null in Tournament Creation"),
    TOURNAMENT_NON_VALID_USER("User is not in a Student (id %d) in Tournament Creation"),
    TOURNAMENT_INVALID_NUM_QUESTS("Number of Questions is a non-positive number in Tournament Creation"),
    TOURNAMENT_INVALID_TOPIC("A Specified Topic does not exist while Creating Tournament"),
    TOURNAMENT_INVALID_STARTTIME("Start Time is out-of-format or not a valid TimeStamp in Tournament Creation"),
    TOURNAMENT_INVALID_TIMEFRAME("The specified TimeFrame in Tournament Creation is not valid."),
    CLARIFICATION_REQUEST_NOT_FOUND("Clarification resquest not found with id %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    CLARIFICATION_REQUEST_NOT_DEFINED("Clarification request is not defined"),
    CLARIFICATION_REQUEST_ANSWER_TYPE_NOT_DEFINED("Clarification request answer type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),

    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),

    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %d not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),
    CLARIFICATION_REQUEST_NO_LONGER_AVAILABLE("This clarification request is no longer available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    ONE_CORRECT_OPTION_NEEDED("Questions need to have 1 and only 1 correct option"),
    CANNOT_CHANGE_ANSWERED_QUESTION("Can not change answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_ALREADY_STARTED("Quiz was already started"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    QUESTION_ANSWER_MISMATCH_USER("Question answer %s is not assigned to student %s"),
    QUESTION_ANSWER_MISMATCH_QUESTION("Question answer %s is not assigned to question %s"),
    QUESTION_ANSWER_NOT_DEFINED("Question answer is not defined"),
    CLARIFICATION_REQUEST_IS_EMPTY("The clarification request content is empty"),
    CLARIFICATION_REQUEST_ANSWER_CONTENT_IS_EMPTY("The clarification request answer content must be defined"),

    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    USER_IS_TEACHER("This operation is invalid to users of the type teacher"),
    USER_IS_STUDENT("This operation is invalid to users of the type student"),
    INVALID_NULL_ARGUMENTS_SUGGESTION("The question suggestion dto given is null"),
    INVALID_NULL_ARGUMENTS_SUGGESTIONID("The question suggestion Id given is null"),
    INVALID_NULL_ARGUMENTS_USERID("The User id given is null"),
    INVALID_NULL_ARGUMENTS_COUSEID("The Course id given given is null"),
    INVALID_NULL_ARGUMENTS_JUTIFICATIONDTO("The justification dto given given is null"),
    USER_NOT_IN_COURSE("The student is not enrolled in the given course"),

    QUESTION_SUGGESTION_ALREADY_ACCEPTED("This suggested question was already accepted"),
    QUESTION_SUGGESTION_ALREADY_REJECTED("This suggested question was already rejected"),
    QUESTION_SUGGESTION_NOT_FOUND("Question suggestion not found with id %d"),
    JUSTIFICATION_MISSING_DATA("Missing information for justification");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}