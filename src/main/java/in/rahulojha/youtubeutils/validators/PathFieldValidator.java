package in.rahulojha.youtubeutils.validators;


import in.rahulojha.youtubeutils.entity.Details;
import in.rahulojha.youtubeutils.entity.ValidationResponse;
import in.rahulojha.youtubeutils.enums.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PathFieldValidator implements FieldValidator {

    private static final String fieldName = "details.path";
    private static final Tag myTag = Tag.PATH;

    @Override
    public ValidationResponse validate(Details details) {
        if (details.getTags().stream().anyMatch(myTag::equals)) {
            if (StringUtils.isBlank(details.getPath())) {
                return ValidationResponse.failure(fieldName);
            }

            Path path = Paths.get(details.getPath());

            if (Files.exists(path)) {
                if (Files.isDirectory(path)) {
                    return ValidationResponse.success(fieldName, String.format("The path '%s' is a valid directory.", path));
                } else {
                    return ValidationResponse.success(fieldName, String.format("The path '%s' is not a valid directory.", path));
                }
            }
            return ValidationResponse.failure(fieldName, String.format("The given path '%s' does not exist.", path));
        }
        return ValidationResponse.ignore(fieldName);
    }
}
