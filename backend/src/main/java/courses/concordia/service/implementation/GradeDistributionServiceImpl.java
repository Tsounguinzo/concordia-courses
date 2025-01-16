package courses.concordia.service.implementation;

import courses.concordia.dto.model.gradedistribution.GradeDistributionDto;
import courses.concordia.model.GradeDistribution;
import courses.concordia.repository.GradeDistributionRepository;
import courses.concordia.service.GradeDistributionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeDistributionServiceImpl implements GradeDistributionService {
    private final GradeDistributionRepository gradeDistributionRepository;
    private final ModelMapper modelMapper;


    /**
     * Get grade distribution of the most recent year
     * @param courseSubject course subject
     * @param courseCatalog course catalog
     * @return grade distribution
     */
    @Cacheable(value = "gradeDistribution", key = "{#courseSubject, #courseCatalog}", unless = "#result == null")
    @Override
    public GradeDistributionDto getGradeDistribution(String courseSubject, String courseCatalog) {
        log.info("Fetching grade distribution for course: {}", courseSubject + courseCatalog);
        GradeDistribution gradeDistribution = gradeDistributionRepository.findTopByCourseSubjectAndCourseCatalogOrderByYearDescTermAsc(courseSubject, courseCatalog);

        if (gradeDistribution != null) {
            return modelMapper.map(gradeDistribution, GradeDistributionDto.class);
        } else {
            return null;
        }
    }
}
