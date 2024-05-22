package courses.concordia.dto.model.gradedistribution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import courses.concordia.model.GradeDistribution;
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
public class GradeDistributionDto {
    private String _id;
    private GradeDistribution.Course course;
    private String term;
    private int year;
    private double classAverage;
    private int classSize;
    private List<GradeDistribution.Grade> grades;
}