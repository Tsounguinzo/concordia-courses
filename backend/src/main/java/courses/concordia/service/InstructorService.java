package courses.concordia.service;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.model.instructor.InstructorReviewsDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InstructorService {
    List<InstructorDto> getInstructors();
    List<InstructorDto> getInstructorsWithFilter(int limit, int offset, InstructorFilterDto filters);
    InstructorDto getInstructorById(String id);
    InstructorReviewsDto getInstructorAndReviewsById(String id);
    void updateInstructorsStatistics();
    void uploadInstructors(MultipartFile file);
}
