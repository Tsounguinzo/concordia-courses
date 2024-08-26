package courses.concordia.service;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.CourseReviewsDto;
import courses.concordia.dto.model.course.CourseFilterDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<CourseDto> getCourses();
    List<CourseDto> getCoursesWithFilter(int limit, int offset, CourseFilterDto filters);
    CourseDto getCourseById(String id);
    CourseReviewsDto getCourseAndReviewsById(String id);
    void updateCourses(MultipartFile file);
    void updateCoursesStatistics();
}
