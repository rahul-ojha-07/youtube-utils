package in.rahulojha.youtubeutils.config;


import in.rahulojha.youtubeutils.constants.ApplicationConstants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Order(1)
@ConditionalOnProperty(name = "app.config.look-in-path")
public class FindExecutableOnPath implements ApplicationRunner {
    public String findExecutableOnPath(String name) {
        for (String dirname : System.getenv("PATH").split(File.pathSeparator)) {
            File file = new File(dirname, name);
            if (file.isFile() && file.canExecute()) {
                return file.getAbsolutePath();
            }
        }
        return null;
    }

    @Override
    public void run(ApplicationArguments args) {
        String pathForYtDlp = findExecutableOnPath(ApplicationConstants.getYtDLPBinaryNameBasedOnOs());
        if (pathForYtDlp != null) {
            System.setProperty("ytDlp_path", pathForYtDlp);
        }

        String pathForFmpeg = findExecutableOnPath(ApplicationConstants.getFfmpegBinaryNameBasedOnOs());
        if (pathForFmpeg != null) {
            System.setProperty("ffmpeg_path", pathForFmpeg);
        }

    }
}
