package courses.concordia.dto.model.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockDto {
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
