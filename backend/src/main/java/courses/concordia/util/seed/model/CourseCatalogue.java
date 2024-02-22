package courses.concordia.util.seed.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseCatalogue {
    private String ID;
    private String title;
    private String subject;
    private String catalog;
    private String career;
    private String classUnit;
    private String prerequisites;
}
