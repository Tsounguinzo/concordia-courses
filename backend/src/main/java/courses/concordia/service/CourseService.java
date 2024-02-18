package courses.concordia.service;

import courses.concordia.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getCourses();
    Course getCourseById(String id);
}
