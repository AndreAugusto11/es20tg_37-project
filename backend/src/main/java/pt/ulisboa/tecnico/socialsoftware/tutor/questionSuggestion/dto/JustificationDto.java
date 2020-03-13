package pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionSuggestion.domain.Justification;

import java.io.Serializable;

public class JustificationDto implements Serializable {

    private Integer id;
    private Integer key;
    private ImageDto imageDto;
    private String content;

    public JustificationDto() {
    }

    public JustificationDto(Justification justification) {
        this.id = justification.getId();
        this.key = justification.getKey();
        this.content = justification.getContent();

        if (justification.getImage() != null)
            this.imageDto = new ImageDto(justification.getImage());
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public ImageDto getImage() { return imageDto; }

    public void setImage(ImageDto imageDto) { this.imageDto = imageDto; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }
}
