package in.rahulojha.youtubeutils.controller;


import in.rahulojha.youtubeutils.entity.Details;
import in.rahulojha.youtubeutils.enums.Tag;
import in.rahulojha.youtubeutils.service.YoutubeUtilsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
@RestController("/")
public class YoutubeUtilsController {

    @NonNull
    private final YoutubeUtilsService utilsService;

    @PostMapping("getDetails")
    public String getDetails(@RequestBody Details details) throws IOException, InterruptedException {
        details.setTags(List.of(Tag.URL, Tag.DETAIL));
        return utilsService.getDetails(details);
    }
    @PostMapping("downloadAudio")
    public ResponseEntity<Object> downloadAudio(@RequestBody Details details) throws IOException, InterruptedException {
        details.setTags(List.of(Tag.AUDIO, Tag.URL));
        return ResponseEntity.ok(utilsService.downloadUsingDetails(details));
    }
    @PostMapping("downloadAudioWithPath")
    public ResponseEntity<Object> downloadAudioWithPath(@RequestBody Details details) throws IOException, InterruptedException {
        details.setTags(List.of(Tag.AUDIO, Tag.URL, Tag.PATH));
        return ResponseEntity.ok(utilsService.downloadUsingDetails(details));
    }
}
