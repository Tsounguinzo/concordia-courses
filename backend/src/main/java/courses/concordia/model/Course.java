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
    private String prerequisites;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    private double avgDifficulty;
    private double avgRating;
    private int reviewCount;
    private Schedule[] schedules;

    @Data
    @AllArgsConstructor
    public static class Block {
        private String componentCode;
        private String locationCode;
        private String roomCode;
        private String section;
        private int classAssociation;
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
        private Block[] blocks;
        private String term;
    }
}
