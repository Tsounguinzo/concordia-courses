package courses.concordia.util.seed.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDescriptionSegment {
    private String description;
    private String prerequisites;
    private String corequisites;
    private String restrictions;

}
