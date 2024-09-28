package in.rahulojha.youtubeutils;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SpringBootApplication
@Log4j2
public class YoutubeUtilsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YoutubeUtilsApplication.class, args);
//        FfmpegDownloader.run();
    }

}
