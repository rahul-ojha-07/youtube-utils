package in.rahulojha.youtubeutils.config;

import in.rahulojha.youtubeutils.exception.YoutubeUtilsException;
import in.rahulojha.youtubeutils.utils.DownloadUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import in.rahulojha.youtubeutils.constants.ApplicationConstants;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static in.rahulojha.youtubeutils.constants.ApplicationConstants.OS_NAME;
import static in.rahulojha.youtubeutils.constants.ApplicationConstants.YTDLP_PATH;

@Component
@Log4j2
@RequiredArgsConstructor
@Order(2)
public class YtDLPChecker implements ApplicationRunner {



    @NonNull
    private AppConfig appConfig;


    private static String VERSION = "--version";
    private String ytDLPPath;
    private String ytDLPBinaryInstallationPath;


    private void setYtDlpInstallPath() {
        ytDLPPath = appConfig.getYtDlpPath();
        if (StringUtils.isBlank(ytDLPPath)) {
            if (DownloadUtils.isValidPath(ytDLPPath)) {
                log.info("Using existing yt-dlp installation path: {}", ytDLPPath);
            } else if (DownloadUtils.isBinaryValid(getYtDLPExecutable(ytDLPPath), VERSION)) {
                log.info("Using existing yt-dlp installation path with binary: {}", ytDLPPath);
            } else {
                log.warn("Existing path is invalid: {}", ytDLPPath);
            }
        }
    }

    public String getYtDLPExecutable(String path) {
        if (!path.endsWith(ApplicationConstants.getYtDLPBinaryNameBasedOnOs())) {
            Path originalPath = Paths.get(path);
            return originalPath.resolve(ApplicationConstants.getYtDLPBinaryNameBasedOnOs()).toString();
        }
        return path;
    }

    public void checkAndDownloadYtDlp() throws InterruptedException {
        if (!DownloadUtils.isBinaryValid(getYtDLPExecutable(ytDLPPath), VERSION)) {
            downloadYtDlp(ytDLPPath, ApplicationConstants.getYtDLPBinaryNameBasedOnOs());
        } else {
            log.info("yt-dlp is already installed.");
        }
    }

    private void downloadYtDlp(String binaryPath, String binaryName) throws InterruptedException {
        try {
            log.info("Downloading yt-dlp...");
            String url = ApplicationConstants.getYtDLPDownloadUrlBasedOnOs();
            String downloadFilePath = Paths.get(ytDLPPath, binaryName).toString();
            DownloadUtils.downloadFile(url, downloadFilePath);
            DownloadUtils.postDownloadActions(binaryPath, binaryName);
        } catch (IOException  e) {
            log.error("Failed to complete downloadYtDlp Method : {}", e.getMessage());
            throw new YoutubeUtilsException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (InterruptedException e) {
            log.error("Failed to complete downloadYtDlp Method: got interrupted");
            throw e;
        }
    }
    

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("yt-dlp Checker Starting");
        if (System.getProperty(YTDLP_PATH) == null) {
            setYtDlpInstallPath();
            System.setProperty(YTDLP_PATH, getYtDLPExecutable(ytDLPPath));
            checkAndDownloadYtDlp();    
        } else {
            log.info("yt-dlp path is set to {}", System.getProperty(YTDLP_PATH));
        }
        log.info("yt-dlp Checker Ending");
    }
}
