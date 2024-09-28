package in.rahulojha.youtubeutils.service;

import in.rahulojha.youtubeutils.entity.Details;

import java.io.IOException;

public interface YoutubeUtilsService {
    String getDetails(Details details) throws InterruptedException, IOException;
    boolean downloadUsingDetails(Details details) throws InterruptedException, IOException;
}
