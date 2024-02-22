package courses.concordia.util.seed.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseWithTermsAndInstructors {
    private String title;
    private String subject;
    private String catalog;
    private List<String> terms;
    private List<String> instructors;
}
