package in.rahulojha.youtubeutils.utils;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

import static in.rahulojha.youtubeutils.constants.ApplicationConstants.OS_NAME;

@Log4j2
public class DownloadUtils {
    public static void downloadFile(String fileUrl, String downloadFilePath) throws IOException {

        String name = fileUrl.split("/")[fileUrl.split("/").length - 1];

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        long fileSize = connection.getContentLengthLong();
        if (fileSize <= 0) {
            log.warn("Could not determine the file size. Proceeding with download anyway.");
        }

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream fos = new FileOutputStream(downloadFilePath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;
            long prevPercentage = 0;
            StringBuilder progressMessage = new StringBuilder();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                // Calculate and log download progress
                long percentage = (fileSize > 0) ? (totalBytesRead * 100 / fileSize) : 0;
                if (percentage > prevPercentage) {
                    logProgress(percentage, prevPercentage, progressMessage, name);
                    prevPercentage = percentage;
                }
            }
        }
    }

    public static boolean isValidPath(String path) {
        File directory = new File(path);
        return directory.exists()
                && directory.isDirectory()
                && directory.canRead()
                && directory.canWrite();
    }

    public static boolean isBinaryValid(String binaryPath, String versionString) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(binaryPath, versionString);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            if ((line = reader.readLine()) != null) {
                log.info("{} version: {}", binaryPath, line);
                return true;
            }
        } catch (IOException e) {
            log.warn("{} is not installed.", binaryPath);
        }
        return false;
    }
    private static final String ANSI_GREEN = "\u001B[32m";
    public static void logProgress(long percentage, long prevPercentage, StringBuilder progressMessage, String name) {
        if (percentage > prevPercentage) {
            progressMessage.append(".".repeat((int) (percentage - prevPercentage)));
            log.info(ANSI_GREEN+ "Downloading {} {} {}%" + ANSI_GREEN,name,
                    String.format("%-100s", progressMessage), percentage);
        }
    }
    public static void postDownloadActions(String binaryPath, String binaryName) throws IOException, InterruptedException {
        if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
            log.info("{} downloaded successfully for Windows.", binaryName);
        } else {
            setFileExecutable(binaryPath, binaryName);
            log.info("{} downloaded and made executable successfully for Unix-like OS.", binaryName);
        }
    }

    private static void setFileExecutable(String binaryPath, String binaryName) throws IOException, InterruptedException {
        String filePath = Paths.get(binaryPath, binaryName).toString();
        ProcessBuilder chmod = new ProcessBuilder("chmod", "+x", filePath);
        chmod.start().waitFor();
    }

}
