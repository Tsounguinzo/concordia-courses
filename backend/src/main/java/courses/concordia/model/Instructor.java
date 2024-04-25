package courses.concordia.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "instructors")
public class Instructor {
    @MongoId
    private String _id;
    private String firstName;
    private String lastName;
    private String department;
    private Set<InstructorCourse> courses;
    private Set<InstructorTag> tags;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgDifficulty = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgRating = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private int reviewCount = 0;

    @Data
    @AllArgsConstructor
    public static class InstructorCourse {
        private String subject;
        private String catalog;
    }

    @Getter
    @AllArgsConstructor
    public enum InstructorTag {
        TOUGH_GRADER("Tough Grader"),
        GET_READY_TO_READ("Get Ready To Read"),
        PARTICIPATION_MATTERS("Participation Matters"),
        EXTRA_CREDIT("Extra Credit"),
        GROUP_PROJECTS("Group Projects"),
        AMAZING_LECTURES("Amazing Lectures"),
        CLEAR_GRADING_CRITERIA("Clear Grading Criteria"),
        GIVES_GOOD_FEEDBACK("Gives Good Feedback"),
        INSPIRATIONAL("Inspirational"),
        LOTS_OF_HOMEWORK("Lots Of Homework"),
        HILARIOUS("Hilarious"),
        BEWARE_OF_POP_QUIZZES("Beware Of Pop Quizzes"),
        SO_MANY_PAPERS("So Many Papers"),
        CARING("Caring"),
        RESPECTED("Respected"),
        FLEXIBLE_DEADLINES("Flexible Deadlines"),
        LECTURE_HEAVY("Lecture Heavy"),
        TEST_HEAVY("Test Heavy"),
        GRADED_BY_FEW_THINGS("Graded By Few Things"),
        ACCESSIBLE_OUTSIDE_CLASS("Accessible Outside Class"),
        ONLINE_SAVVY("Online Savvy"),
        ENGAGING("Engaging"),
        TECHNICALLY_PROFICIENT("Technically Proficient"),
        INDUSTRY_EXPERIENCED("Industry Experienced"),
        RESEARCH_ORIENTED("Research-Oriented"),
        MULTIDISCIPLINARY_APPROACH("Multidisciplinary Approach"),
        INTERACTIVE_SESSIONS("Interactive Sessions"),
        ENCOURAGES_CRITICAL_THINKING("Encourages Critical Thinking"),
        USES_MULTIMEDIA("Uses Multimedia"),
        CULTURALLY_INCLUSIVE("Culturally Inclusive");

        private final String displayName;

        @JsonValue
        public String toValue() {
            return this.displayName;
        }
        public static InstructorTag fromString(String text) {
            for (InstructorTag b : InstructorTag.values()) {
                if (b.displayName.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
