package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionDiscussion.domain.ClarificationRequest;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String url;
    private Integer width;

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    @OneToOne
    @JoinColumn(name="clarification_request_id")
    private ClarificationRequest clarificationRequest;

    public Image() {}

    public Image(ImageDto imageDto) {
        this.url = imageDto.getUrl();
        this.width = imageDto.getWidth();
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public ClarificationRequest getClarificationRequest() { return clarificationRequest; }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", width=" + width +
                '}';
    }
}