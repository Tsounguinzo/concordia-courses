package courses.concordia.service;

import courses.concordia.dto.model.gradedistribution.GradeDistributionDto;

public interface GradeDistributionService {
    GradeDistributionDto getGradeDistribution(String courseSubject, String courseCatalog);
}
