package courses.concordia.service;

import courses.concordia.dto.CourseReviewsDTO;
import courses.concordia.model.Course;
import courses.concordia.model.CourseFilter;

import java.util.List;

public interface CourseService {
    List<Course> getCourses();
    List<Course> getCourses(int limit, int offset, CourseFilter filters);
    Course getCourseById(String id);
    CourseReviewsDTO getCourseAndReviewsById(String id);
}
