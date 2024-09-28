package in.rahulojha.youtubeutils.validators;


import in.rahulojha.youtubeutils.entity.Details;
import in.rahulojha.youtubeutils.entity.ValidationResponse;
import in.rahulojha.youtubeutils.enums.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AudioFieldValidator implements FieldValidator {

    private static final String fieldName = "details.audio";
    private static final Tag myTag = Tag.AUDIO;
    @Override
    public ValidationResponse validate(Details details) {
        if (details.getTags().contains(myTag)) {
            if (StringUtils.isBlank(details.getAudio())) {
                return ValidationResponse.failure(fieldName);
            }
            return ValidationResponse.success(fieldName);
        }
        return ValidationResponse.ignore(fieldName);
    }
}
