package in.rahulojha.youtubeutils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "app.config")
@Data
public class AppConfig {
    private boolean useFfmpeg;
    private String ytDlpPath;
    private String ffmpegPath;
    private String os;
}
