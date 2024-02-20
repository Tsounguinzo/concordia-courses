package courses.concordia.util.seed.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseWithDetails {
    private String courseID;
    private String termCode;
    private String session;
    private String subject;
    private String catalog;
    private String section;
    private String componentCode;
    private String componentDescription;
    private String classNumber;
    private String classAssociation;
    private String courseTitle;
    private String topicID;
    private String topicDescription;
    private String classStatus;
    private String locationCode;
    private String instructionModeCode;
    private String instructionModeDescription;
    private String meetingPatternNumber;
    private String roomCode;
    private String buildingCode;
    private String room;
    private String classStartTime;
    private String classEndTime;
    private String modays;
    private String tuesdays;
    private String wednesdays;
    private String thursdays;
    private String fridays;
    private String saturdays;
    private String sundays;
    private String classStartDate;
    private String classEndDate;
    private String career;
    private String departmentCode;
    private String departmentDescription;
    private String facultyCode;
    private String facultyDescription;
    private String enrollmentCapacity;
    private String currentEnrollment;
    private String waitlistCapacity;
    private String currentWaitlistTotal;
    private String hasSeatReserved;
}
