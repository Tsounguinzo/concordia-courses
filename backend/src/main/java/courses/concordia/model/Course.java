package courses.concordia.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "courses")
public class Course {
    @MongoId
    private String _id;
    private List<String> terms;
    private List<String> instructors;
    private String prerequisites;
    private String subject;
    private String description;
    private String catalog;
    private String title;
    private double classUnit;
    private String ConUCourseID;
    private String notes;
    private String[] resourceLinks;
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgDifficulty = 0.0;
    private int[] difficultyDistribution = new int[5];
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private double avgExperience = 0.0;
    private int[] experienceDistribution = new int[5];
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
        private List<Block> blocks;
        private String term;
    }
}
