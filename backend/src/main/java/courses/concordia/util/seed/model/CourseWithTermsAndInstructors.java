package courses.concordia.util.seed.model;

import lombok.Data;

import java.util.List;

@Data
public class CourseWithTermsAndInstructors {
    private String subject;
    private String catalog;
    private List<String> terms;
    private List<String> instructors;
}
