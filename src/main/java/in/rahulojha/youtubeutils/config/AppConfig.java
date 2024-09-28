package in.rahulojha.youtubeutils.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "app.config")
@Data
public class AppConfig {

    private CustomConfig customConfig;
    private DefaultConfig defaultConfig;


    @Data
    public static class CustomConfig {
        private String ytDlpPath;
        private String ffmpegPath;
        private String os;
    }

    @Data
    public static class DefaultConfig {
        private String ytDlpPathWin;
        private String ffmpegPathWin;
        private String ytDlpPathLinux;
        private String ffmpegPathLinux;
        private String ytDlpPathMac;
        private String ffmpegPathMac;
    }
}
