package in.rahulojha.youtubeutils.constants;

public class ApplicationConstants {

    private ApplicationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String YT_DLP_BINARY_NAME = "yt-dlp";
    public static final String YT_DLP_BINARY_NAME_WIN = YT_DLP_BINARY_NAME + ".exe";
    public static final String FFMPEG_BINARY_NAME = "ffmpeg";
    public static final String FFMPEG_BINARY_NAME_WIN = FFMPEG_BINARY_NAME + ".exe";
    public static final String YTDLP_DOWNLOAD_URL = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp";
    public static final String YTDLP_DOWNLOAD_UR_WIN = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe";

    //https://evermeet.cx/ffmpeg/ffmpeg-7.0.2.7z mac
    public static final String FFMPEG_DOWNLOAD_URL_MAC = "https://evermeet.cx/ffmpeg/ffmpeg-7.0.2.7z";

    public static final String FFMPEG_DOWNLOAD_URL_WIN = "https://github.com/GyanD/codexffmpeg/releases/download/7.0.2/ffmpeg-7.0.2-full_build.7z";

    public static final String OS_NAME = "os.name";

    public static String getBinaryNameBasedOnOs() {
        if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
            return YT_DLP_BINARY_NAME_WIN;
        }
        return YT_DLP_BINARY_NAME;
    }

    public static String getFfmpegBinaryNameBasedOnOs() {
        if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
            return FFMPEG_BINARY_NAME_WIN;
        }
        return FFMPEG_BINARY_NAME;
    }

    public static String getYtdlpDownloadUrlBasedOnOs() {
        if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
            return YTDLP_DOWNLOAD_UR_WIN;
        }
        return YTDLP_DOWNLOAD_URL;
    }

    public static String getFFmpegDownloadUrlBasedOnOs() {
        if (System.getProperty(OS_NAME).toLowerCase().contains("win")) {
            return FFMPEG_DOWNLOAD_URL_WIN;
        }
        return FFMPEG_DOWNLOAD_URL_MAC;
    }

}
