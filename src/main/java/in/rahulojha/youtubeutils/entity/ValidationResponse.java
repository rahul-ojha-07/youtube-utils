package in.rahulojha.youtubeutils.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ValidationResponse {
    public enum StatusEnum {SUCCESS, FAILURE};

    private String fieldName;
    private String message;
    private StatusEnum status = StatusEnum.FAILURE;

    public boolean isSuccess() {
        return status == StatusEnum.SUCCESS;
    }
    public boolean isFailure() {
        return status == StatusEnum.FAILURE;
    }


    public static ValidationResponse success(String fieldName ) {
        return success(fieldName, "Validation Success");
    }
    public static ValidationResponse success(String fieldName, String message) {
        return new ValidationResponse(fieldName, message, StatusEnum.SUCCESS);
    }

    public static ValidationResponse failure(String fieldName) {
        return failure(fieldName, "Validation Failure " + fieldName + " can not be null or empty for this operation");
    }
    public static ValidationResponse failure(String fieldName, String message) {
        return new ValidationResponse(fieldName, message, StatusEnum.FAILURE);
    }

    public static ValidationResponse ignore(String fieldName) {
        return success(fieldName, "No Need to Validate for : " + fieldName);
    }
}
