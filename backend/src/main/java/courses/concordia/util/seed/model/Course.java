package courses.concordia.util.seed.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Course {
    private String _id;
    private List<String> terms;
    private String subject;
    private String description;
    private String prerequisites;
    private String catalog;
    private String title;
    private double classUnit;
    private String ConUCourseID;
    private List<Schedule> schedules;

    @Data
    @AllArgsConstructor
    public static class Block {
        private String componentCode;
        private String locationCode;
        private String roomCode;
        private String section;
        private String session;
        private String buildingCode;
        private String instructionModeCode;
        private String instructionModeDescription;
        private String mondays;
        private String tuesdays;
        private String wednesdays;
        private String thursdays;
        private String fridays;
        private String saturdays;
        private String sundays;
        private String classStartTime;
        private String classEndTime;
        private Integer enrollmentCapacity;
        private Integer currentEnrollment;
        private Integer waitlistCapacity;
        private Integer currentWaitlistTotal;
        private String hasSeatReserved;
    }

    @Data
    @AllArgsConstructor
    public static class Schedule {
        private Set<Block> blocks;
        private String term;
    }
}