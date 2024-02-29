package courses.concordia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Document(collection = "courses")
public class Course {
    @MongoId
    private String _id;
    private List<String> terms;
    private String prerequisites;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private float avgDifficulty = 0.0f;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private float avgRating = 0.0f;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private int reviewCount = 0;
    private List<Schedule> schedules;

    @Data
    @AllArgsConstructor
    public static class Block {
        private String componentCode;
        private String locationCode;
        private String roomCode;
        private String section;
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
    }

    @Data
    @AllArgsConstructor
    public static class Schedule {
        private List<Block> blocks;
        private String term;
    }
}
