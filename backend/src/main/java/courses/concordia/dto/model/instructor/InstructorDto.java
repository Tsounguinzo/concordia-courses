package courses.concordia.dto.model.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import courses.concordia.model.Instructor;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Set;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstructorDto {
    private String _id;
    private String firstName;
    private String lastName;
    private Set<Instructor.Department> departments;
    private Set<Instructor.Course> courses;
    private Set<Instructor.Tag> tags;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgDifficulty = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgRating = 0.0;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private int reviewCount = 0;
}
