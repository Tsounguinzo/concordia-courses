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

import java.util.Comparator;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeDistributionServiceImpl implements GradeDistributionService {
    private final GradeDistributionRepository gradeDistributionRepository;
    private final ModelMapper modelMapper;


    /**
     * Get grade distribution of the most recent year
     * @param courseId course id
     * @return grade distribution
     */
    @Cacheable(value = "gradeDistribution", key = "#courseId", unless = "#result == null")
    @Override
    public GradeDistributionDto getGradeDistribution(String courseId) {
        return gradeDistributionRepository.findAll().stream()
                .max(Comparator.comparingInt(GradeDistribution::getYear))
                .map(gradeDistribution -> modelMapper.map(gradeDistribution, GradeDistributionDto.class))
                .orElse(null);
    }
}
