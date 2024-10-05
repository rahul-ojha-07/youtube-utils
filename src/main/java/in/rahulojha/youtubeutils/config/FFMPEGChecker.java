package in.rahulojha.youtubeutils.config;

import in.rahulojha.youtubeutils.exception.YoutubeUtilsException;
import in.rahulojha.youtubeutils.utils.DownloadUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static in.rahulojha.youtubeutils.constants.ApplicationConstants.*;


@Component
@Log4j2
@RequiredArgsConstructor
@Order(3)
@ConditionalOnProperty(name = "app.config.use-ffmpeg", havingValue = "true")
public class FFMPEGChecker implements ApplicationRunner {

    @NonNull
    private AppConfig appConfig;

    private String ffmpegInstallationPath;

    @Override
    public void run(ApplicationArguments args) throws IOException, InterruptedException {
        log.info("FFMPEG checker Starting");
        if (System.getProperty("ffmpeg_path") == null) {
            setFfmpegInstallPath();
            checkAndDownloadFfmpeg();
        }else {
            log.info("FFMPEG path set to {}", System.getProperty("ffmpeg_path"));
        }
        log.info("FFMPEG checker Ended");
    }

    private void setFfmpegInstallPath() throws IOException {
        ffmpegInstallationPath = appConfig.getFfmpegPath();
        if (!StringUtils.isBlank(ffmpegInstallationPath)) {
            if (DownloadUtils.isValidPath(ffmpegInstallationPath)) {
                log.info("Using existing ffmpeg installation path: {}", ffmpegInstallationPath);
            } else if (!DownloadUtils.isValidPath(ffmpegInstallationPath)) {
                log.error("The installation path is invalid: {}", ffmpegInstallationPath);
                Files.createDirectories(Paths.get(ffmpegInstallationPath));
            }
        }
    }

    public void checkAndDownloadFfmpeg() throws IOException, InterruptedException {
        String ffmpegPath = findExecutableInPath(ffmpegInstallationPath,  getFfmpegBinaryNameBasedOnOs());
        if (null != ffmpegPath) {
            System.setProperty("ffmpeg_path", ffmpegPath);
            ffmpegInstallationPath = ffmpegPath;
            log.info("ffmpeg path set to {}", ffmpegPath);
        }
        if (!DownloadUtils.isBinaryValid(getFfmpegExecutable(ffmpegInstallationPath), "-version")) {
            downloadFfmpeg();
        } else {
            log.info("ffmpeg is already installed.");
        }
    }
    public String getFfmpegExecutable(String path) {
        if (!path.endsWith(getFfmpegBinaryNameBasedOnOs())) {
            Path originalPath = Paths.get(path);
            return originalPath.resolve(getFfmpegBinaryNameBasedOnOs()).toString();
        }
        return path;
    }

    private void downloadFfmpeg() throws IOException, InterruptedException {
        try {
            log.info("Downloading ffmpeg...");
            String ffmpegUrl =  getFFmpegDownloadUrlBasedOnOs();
            String downloadFilePath = Paths.get(ffmpegInstallationPath, "ffmpeg.7z").toString();
            DownloadUtils.downloadFile(ffmpegUrl, downloadFilePath);
            extract7zFile(downloadFilePath, ffmpegInstallationPath);
            DownloadUtils.postDownloadActions(ffmpegInstallationPath, getFfmpegBinaryNameBasedOnOs());
            String ffmpegPath = findExecutableInPath(ffmpegInstallationPath,  getFfmpegBinaryNameBasedOnOs());
            if (null != ffmpegPath) {
                System.setProperty("ffmpeg_path", ffmpegPath);
                log.info("ffmpeg path set to {}", ffmpegPath);
            }
        } catch (IOException  e) {
            log.error("Failed to complete downloadFfmpeg Method : {}", e.getMessage());
            throw new YoutubeUtilsException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (InterruptedException e) {
            log.error("Failed to complete downloadFfmpeg Method: got interrupted");
            throw e;
        }
    }

    private static String findExecutableInPath(String pathString, String executable) throws IOException {
        final String[] requiredFile = {null};
        Path path = Paths.get(pathString);
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().equalsIgnoreCase(executable)) {
                    requiredFile[0] = file.toAbsolutePath().toString();
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return requiredFile[0];
    }

    public void extract7zFile(String sourceFile, String destinationDir) throws IOException, InterruptedException {
        File destDir = new File(destinationDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        String command = String.format("7z x \"%s\" -o\"%s\"", sourceFile, destinationDir);

        ProcessBuilder processBuilder = new ProcessBuilder();
        if (System.getProperty(OS_NAME).contains("win") || System.getProperty(OS_NAME).contains("Win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new YoutubeUtilsException(HttpStatus.INTERNAL_SERVER_ERROR,"Error extracting file: " + sourceFile);
        }
    }

}
