package in.rahulojha.youtubeutils.service.impl;

import in.rahulojha.youtubeutils.entity.Details;
import in.rahulojha.youtubeutils.entity.ValidationResponse;
import in.rahulojha.youtubeutils.enums.Tag;
import in.rahulojha.youtubeutils.service.YoutubeUtilsService;
import in.rahulojha.youtubeutils.validators.FieldValidator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Log4j2
@Service
@AllArgsConstructor
public class YoutubeUtilsServiceImpl implements YoutubeUtilsService {

    List<FieldValidator> fieldValidator;

    private static final ProcessBuilder processBuilder = new ProcessBuilder();

    @Override
    public String getDetails(Details details) throws InterruptedException, IOException{
        int exitCode = -1;
        StringBuilder output = new StringBuilder();
        try {
            List<String> command = buildCommand(details);
            ProcessBuilder pb = processBuilder.command(command);
            exitCode = commandRunner(pb, output);
            if (exitCode == 0)
                return  output.toString();
        } catch (InterruptedException | IOException exception) {
            log.error("ERROR", exception);
            throw exception;
        }
        return "ERROR :: " + exitCode;
    }

    @Override
    public boolean downloadUsingDetails(Details details) throws InterruptedException, IOException {
        int exitCode = -1;
        StringBuilder output = new StringBuilder();
        try {
            List<String> command = buildCommand(details);
            ProcessBuilder pb = processBuilder.command(command);
            exitCode = commandRunner(pb, output);
        } catch ( InterruptedException | IOException exception) {
            log.error("ERROR" , exception);
            throw exception;
        }
        return exitCode == 0;
    }

    private int commandRunner(ProcessBuilder processBuilder,StringBuilder output ) throws InterruptedException, IOException {
        Process process = processBuilder.start();
        try(BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line = null;
            while( (line = br.readLine()) != null) {
                log.debug(line);
                output.append(line).append("\n");
            }
        }
        return process.waitFor();
    }

    private void validateDetails(Details details) throws IllegalArgumentException {
        String validationMessage = fieldValidator.stream()
                .map(fv -> fv.validate(details))
                .filter(ValidationResponse::isFailure)
                .peek(this::logValidationResponse)
                .map(ValidationResponse::getMessage)
                .collect(Collectors.joining(", "));
        if (!validationMessage.isEmpty()) {
            throw new IllegalArgumentException(validationMessage);
        }
    }


    private List<String> buildCommand(Details details) {
        if (details.getTags() == null || details.getTags().isEmpty()) {
            throw new IllegalArgumentException("Tags are empty");
        }
        this.validateDetails(details);
        List<String> command = new ArrayList<>(List.of("yt-dlp"));
        List<Tag> tags = details.getTags();

        // Check for specific tags and build command accordingly
        if (tags.contains(Tag.PATH)) {
            command.add("-P");
            command.add(details.getPath());
        }
        if (tags.contains(Tag.DETAIL)) {
            command.add("-F");
        }

        if (tags.contains(Tag.AUDIO_VIDEO)) {
            command.add("-f");
            command.add(details.getAudio() + "+" + details.getVideo());
        } else if (tags.contains(Tag.VIDEO)) {
            command.add("-f");
            command.add(details.getVideo());
        } else if (tags.contains(Tag.AUDIO)) {
            command.add("-f");
            command.add(details.getAudio());
        }

        if (tags.contains(Tag.URL)) {
            command.add(details.getUrl());
        }
        return command;
    }


    private void logValidationResponse(ValidationResponse validationResponse) {
        log.error("Invalid {}: {} ", validationResponse.getFieldName(), validationResponse.getMessage());
    }

}
