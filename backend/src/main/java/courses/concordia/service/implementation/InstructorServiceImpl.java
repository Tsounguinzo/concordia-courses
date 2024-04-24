package courses.concordia.service.implementation;

import courses.concordia.dto.model.instructor.InstructorDto;
import courses.concordia.dto.model.instructor.InstructorFilterDto;
import courses.concordia.dto.model.instructor.InstructorReviewsDto;
import courses.concordia.repository.InstructorRepository;
import courses.concordia.service.InstructorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class InstructorServiceImpl implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final MongoTemplate mongoTemplate;
    private final ModelMapper modelMapper;

    @Override
    public List<InstructorDto> getInstructors() {
        return null;
    }

    @Override
    public List<InstructorDto> getInstructorsWithFilter(int limit, int offset, InstructorFilterDto filters) {
        return null;
    }

    @Override
    public InstructorDto getInstructorByName(String name) {
        return null;
    }

    @Override
    public InstructorReviewsDto getInstructorAndReviewsByName(String name) {
        return null;
    }
}
