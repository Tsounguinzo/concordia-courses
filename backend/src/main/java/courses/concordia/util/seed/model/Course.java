package courses.concordia.util.seed.model;

import courses.concordia.model.Instructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Course {
    private String _id;
    private List<String> terms;
    private List<Instructor> instructors;
    private String prerequisites;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    private List<Schedule> schedules;

    @Data
    @AllArgsConstructor
    public static class Block {
        private String componentCode;
        private String locationCode;
        private String roomCode;
        private String section;
        private String classAssociation;
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
    }

    @Data
    @AllArgsConstructor
    public static class Schedule {
        private List<Block> blocks;
        private String term;
    }
}