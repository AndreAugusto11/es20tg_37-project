package pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;

import javax.persistence.*;

@Entity
@Table(name = "public_clarification_requests")
public class PublicClarificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne
    @JoinColumn(name = "clarification_request_id")
    private ClarificationRequest clarificationRequest;

    public PublicClarificationRequest() {
    }

    public PublicClarificationRequest(Course course, ClarificationRequest clarificationRequest) {
        this.course = course;
        this.clarificationRequest = clarificationRequest;
    }

    public Integer getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ClarificationRequest getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }
}
