package courses.concordia.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewProcessingResult {
    private int addedCount;
    private int failedCount;
    private int alreadyExistsCount;
    private List<String> errors = new ArrayList<>();

    public void incrementAdded() {
        addedCount++;
    }

    public void incrementFailed() {
        failedCount++;
    }

    public void incrementAlreadyExists() {
        alreadyExistsCount++;
    }

    public void addError(String error) {
        errors.add(error);
    }
}

