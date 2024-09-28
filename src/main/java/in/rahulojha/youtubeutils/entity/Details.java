package in.rahulojha.youtubeutils.entity;


import in.rahulojha.youtubeutils.enums.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Details{
    private String url;
    private String audio;
    private String video;
    private String path;
    private List<Tag> tags;
}

