package in.rahulojha.youtubeutils.validators;

import in.rahulojha.youtubeutils.entity.Details;
import in.rahulojha.youtubeutils.entity.ValidationResponse;
import org.springframework.stereotype.Component;

@Component
public interface FieldValidator {
    String GENERIC_MESSAGE_FOR_FIELD_VALIDATOR = "Validations Failed for ::";
    ValidationResponse validate(final Details details);
}
