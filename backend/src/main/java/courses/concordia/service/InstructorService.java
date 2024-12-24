package courses.concordia.service;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.model.instructor.InstructorReviewsDto;
import courses.concordia.dto.model.review.ReviewSortingDto;
import courses.concordia.dto.response.ProcessingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InstructorService {
    List<InstructorDto> getInstructorsWithFilter(int limit, int offset, InstructorFilterDto filters);
    InstructorDto getInstructorById(String id);
    void updateInstructorsStatistics();
    ProcessingResult uploadInstructors(MultipartFile file);
    InstructorReviewsDto getInstructorAndReviewsByIdPaginated(String id, int limit, int offset, String userId, ReviewSortingDto sortType);
}
