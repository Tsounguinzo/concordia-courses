package courses.concordia.dto.model.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseDto {
    private String _id;
    private List<String> terms;
    private String prerequisites;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgDifficulty = 0.0f;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgRating = 0.0f;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private int reviewCount = 0;
    private List<ScheduleDto> schedules;
}