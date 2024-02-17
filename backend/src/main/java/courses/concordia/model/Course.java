package courses.concordia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "courses")
public class Course {
    @MongoId
    private String _id;
    private String[] terms;
    private Instructor[] instructors;
    private String classUnit;
    private String prerequisites;
    private String crosslisted;
    private String session;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    private String topicID;
    private String topicDescription;
    private String classStatus;
    private String career;
    private String departmentCode;
    private String departmentDescription;
    private String facultyCode;
    private String facultyDescription;
    private Schedule[] schedules;

    @Data
    @AllArgsConstructor
    public static class Block {
        private String componentCode;
        private String componentDescription;
        private String locationCode;
        private String roomCode;
        private String buildingCode;
        private String room;
        private String section;
        private int classNumber;
        private int classAssociation;
        private String instructionModeCode;
        private String instructionModeDescription;
        private String meetingPatternNumber;
        private boolean mondays;
        private boolean tuesdays;
        private boolean wednesdays;
        private boolean thursdays;
        private boolean fridays;
        private boolean saturdays;
        private boolean sundays;
        private String classStartTime;
        private String classEndTime;
        private String classStartDate;
        private String classEndDate;
    }

    @Data
    @AllArgsConstructor
    public static class Schedule {
        private Block[] blocks;
        private String term;
    }
}
