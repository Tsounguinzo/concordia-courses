package courses.concordia.repository;

import courses.concordia.model.Instructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface InstructorRepository extends MongoRepository<Instructor, String> {
    List<Instructor> findByCoursesContaining(Set<Instructor.Course> courses);
}
