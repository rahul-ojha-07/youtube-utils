package in.rahulojha.youtubeutils.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class YoutubeUtilsException extends ResponseStatusException {
    public YoutubeUtilsException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}
