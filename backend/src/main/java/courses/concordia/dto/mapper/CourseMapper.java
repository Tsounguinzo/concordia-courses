package courses.concordia.dto.mapper;

import courses.concordia.dto.model.course.BlockDto;
import courses.concordia.dto.model.course.CourseDto;
import courses.concordia.dto.model.course.ScheduleDto;
import courses.concordia.model.Course;

import java.util.stream.Collectors;

public class CourseMapper {

    public static CourseDto toDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.set_id(course.get_id());
        dto.setTerms(course.getTerms());
        dto.setInstructors(course.getInstructors());
        dto.setPrerequisites(course.getPrerequisites());
        dto.setSubject(course.getSubject());
        dto.setDescription(course.getDescription());
        dto.setCatalog(course.getCatalog());
        dto.setTitle(course.getTitle());
        dto.setAvgDifficulty(course.getAvgDifficulty());
        dto.setAvgExperience(course.getAvgExperience());
        dto.setDifficultyDistribution(course.getDifficultyDistribution());
        dto.setExperienceDistribution(course.getExperienceDistribution());
        dto.setReviewCount(course.getReviewCount());
        dto.setSchedules(course.getSchedules().stream().map(CourseMapper::toScheduleDto).collect(Collectors.toList()));
        return dto;
    }

    private static ScheduleDto toScheduleDto(Course.Schedule schedule) {
        ScheduleDto dto = new ScheduleDto();
        dto.setTerm(schedule.getTerm());
        dto.setBlocks(schedule.getBlocks().stream().map(CourseMapper::toBlockDto).collect(Collectors.toList()));
        return dto;
    }

    private static BlockDto toBlockDto(Course.Block block) {
        BlockDto dto = new BlockDto();
        dto.setComponentCode(block.getComponentCode());
        dto.setLocationCode(block.getLocationCode());
        dto.setRoomCode(block.getRoomCode());
        dto.setSection(block.getSection());
        dto.setClassStartTime(block.getClassStartTime());
        dto.setClassEndTime(block.getClassEndTime());
        return dto;
    }
}
