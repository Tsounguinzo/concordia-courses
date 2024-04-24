package courses.concordia.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

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
    private List<InstructorCourse> courses;
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
}
