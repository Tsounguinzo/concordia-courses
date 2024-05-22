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
@Document(collection = "gradeDistributions")
public class GradeDistribution {
    @MongoId
    private String _id;
    private Course course;
    private String term;
    private int year;
    private int classAverage;
    private int classSize;
    private List<Grade> grades;

    @Data
    @AllArgsConstructor
    public static class Grade {
        private String grade;
        private int count;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Course {
        private String subject;
        private String catalog;
    }
}