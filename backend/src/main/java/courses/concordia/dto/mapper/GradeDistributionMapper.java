package courses.concordia.dto.mapper;

import courses.concordia.dto.model.gradedistribution.GradeDistributionDto;
import courses.concordia.model.GradeDistribution;

public class GradeDistributionMapper {
    public static GradeDistributionDto toDto(GradeDistribution gradeDistribution) {
        return new GradeDistributionDto()
                .set_id(gradeDistribution.get_id())
                .setCourse(gradeDistribution.getCourse())
                .setTerm(gradeDistribution.getTerm())
                .setYear(gradeDistribution.getYear())
                .setClassAverage(gradeDistribution.getClassAverage())
                .setClassSize(gradeDistribution.getClassSize())
                .setGrades(gradeDistribution.getGrades());
    }

    public static GradeDistribution toModel(GradeDistributionDto gradeDistributionDto) {
        return new GradeDistribution()
                .set_id(gradeDistributionDto.get_id())
                .setCourse(gradeDistributionDto.getCourse())
                .setTerm(gradeDistributionDto.getTerm())
                .setYear(gradeDistributionDto.getYear())
                .setClassAverage(gradeDistributionDto.getClassAverage())
                .setClassSize(gradeDistributionDto.getClassSize())
                .setGrades(gradeDistributionDto.getGrades());
    }
}
