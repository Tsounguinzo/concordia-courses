package courses.concordia.service;

import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.CourseFilterDto;
import courses.concordia.dto.model.course.CourseReviewsDto;
import courses.concordia.dto.model.instructor.CourseInstructorDto;
import courses.concordia.dto.model.review.ReviewSortingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    List<CourseDto> getCoursesWithFilter(int limit, int offset, CourseFilterDto filters);
    CourseDto getCourseById(String id);
    CourseReviewsDto getCourseAndReviewsById(String id);
    void updateCourses(MultipartFile file);
    void updateCoursesStatistics();
    List<CourseInstructorDto> getInstructors(String id);
    CourseReviewsDto getCourseAndReviewsByIdPaginated(String id, int limit, int offset, ReviewSortingDto sortType);
}
