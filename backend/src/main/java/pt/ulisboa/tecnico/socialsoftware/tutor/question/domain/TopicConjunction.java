package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_RESULTS_DATE_FOR_TOURNAMENT;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOPIC_CONJUNCTION_IS_USED_IN_TOURNAMENT;

@Entity
@Table(name = "topic_conjunctions")
public class TopicConjunction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "topicConjunctions")
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    public Integer getId() {
        return id;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    public void remove() {
        if (this.tournament != null && this.assessment != null) {
            throw new TutorException(TOPIC_CONJUNCTION_IS_USED_IN_TOURNAMENT, this.tournament.getTitle());
        }

        getTopics().forEach(topic -> topic.getTopicConjunctions().remove(this));
        getTopics().clear();

        if (this.assessment != null) {
            this.assessment.getTopicConjunctions().remove(this);
            this.assessment = null;
        } else if (this.tournament != null) {
            this.tournament.getTopicConjunctions().remove(this);
            this.tournament = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicConjunction that = (TopicConjunction) o;
        return id.equals(that.id) &&
                Objects.equals(topics, that.topics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, topics);
    }

    @Override
    public String toString() {
        return "TopicConjunction{" +
                "id=" + id +
                ", topics=" + topics +
                '}';
    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getTopicConjunctions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.addTopicConjunction(this);
        });
    }

    public Set<Question> getQuestions() {
        return this.topics.stream()
                .flatMap(topic -> topic.getQuestions().stream())
                .filter(question -> question.getTopics().equals(this.topics))
                .collect(Collectors.toSet());
    }
}