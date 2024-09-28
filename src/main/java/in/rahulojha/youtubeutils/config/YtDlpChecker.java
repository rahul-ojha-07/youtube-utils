package in.rahulojha.youtubeutils.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static in.rahulojha.youtubeutils.constants.ApplicationConstants.getBinaryNameBasedOnOs;
import static in.rahulojha.youtubeutils.constants.ApplicationConstants.getYtdlpDownloadUrlBasedOnOs;

@Component
@Log4j2
@RequiredArgsConstructor
public class YtDlpChecker  implements ApplicationRunner {



    @NonNull
    private AppConfig appConfig;

    private String ytDlpInstallationPath;


    private void setYtDlpInstallPath() {
        ytDlpInstallationPath = appConfig.getCustomConfig().getYtDlpPath();
        if (StringUtils.isBlank(ytDlpInstallationPath)) {
            if (isValidPath(ytDlpInstallationPath)) {
                log.info("Using existing yt-dlp installation path: {}", ytDlpInstallationPath);
                return;
            } else {
                log.warn("Existing path is invalid: {}", ytDlpInstallationPath);
            }
        }
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            ytDlpInstallationPath =  appConfig.getDefaultConfig().getYtDlpPathWin();
        } else if (os.contains("mac")) {
            ytDlpInstallationPath =  appConfig.getDefaultConfig().getYtDlpPathMac();
        } else if (os.contains("nix") || os.contains("nux")) {
            ytDlpInstallationPath = appConfig.getDefaultConfig().getYtDlpPathLinux();
        } else {
            log.error("Unsupported operating system: {}", os);
            System.exit(1);
        }

        if (!isValidPath(ytDlpInstallationPath)) {
            log.error("The installation path is invalid: {}", ytDlpInstallationPath);
            System.exit(1);
        }

        if (!ytDlpInstallationPath.endsWith(getBinaryNameBasedOnOs())) {
            Path originalPath = Paths.get(ytDlpInstallationPath);
            ytDlpInstallationPath = originalPath.resolve(getBinaryNameBasedOnOs()).toString();
        }
    }

    private boolean isValidPath(String path) {
        File directory = new File(path);
        return directory.exists()
                && directory.isDirectory()
                && directory.canRead()
                && directory.canWrite();
    }

    public void checkAndDownloadYtDlp() {
        if (!isYtDlpInstalled()) {
            downloadYtDlp();
        } else {
            log.info("yt-dlp is already installed.");
        }
    }

    private boolean isYtDlpInstalled() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(ytDlpInstallationPath, "--version");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                log.info("yt-dlp version: {}", line);
                return true;
            }
        } catch (IOException e) {
            log.warn("yt-dlp is not installed.");
        }
        return false;
    }

    private void downloadYtDlp() {
        try {
            log.info("Downloading yt-dlp...");
            URL url = new URL(getYtdlpDownloadUrlBasedOnOs());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            long fileSize = connection.getContentLength();

            try (FileOutputStream fos = new FileOutputStream(ytDlpInstallationPath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalByteRead = 0;
                StringBuilder progressMessage = new StringBuilder();
                while ((bytesRead = connection.getInputStream().read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalByteRead += buffer.length;

                    long percentage = (long)Math.floor((totalByteRead / (double)fileSize) * 100);
                    progressMessage.setLength(0);
                    progressMessage.append(String.format("Downloading yt-dlp... %d%%", percentage));

                    log.info("\r" + progressMessage);
                }
            }



            // Set the file as executable or rename for Windows
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                log.info("yt-dlp downloaded successfully for Windows.");
            } else {
                ProcessBuilder chmod = new ProcessBuilder("chmod", "+x", ytDlpInstallationPath);
                chmod.start();
                log.info("yt-dlp downloaded and made executable successfully for Unix-like OS.");
            }
        } catch (IOException e) {
            log.error("Failed to download yt-dlp: {}", e.getMessage());
        }
    }

    private static final String FFMPEG_DOWNLOAD_URL = "https://ffmpeg.org/releases/ffmpeg-release-full.7z";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        setYtDlpInstallPath();
        checkAndDownloadYtDlp();
    }
}
