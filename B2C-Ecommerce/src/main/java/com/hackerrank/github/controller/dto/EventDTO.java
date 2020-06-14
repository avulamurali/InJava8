package com.hackerrank.github.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackerrank.github.model.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String type;
    private ActorDTO actor;
    private RepoDTO repo;
    @JsonProperty(value = "created_at")
    private String createdAt;

    public static EventDTO convertFrom(Event event) {
        return new EventDTO(
                event.getId(),
                event.getType(),
                ActorDTO.convertFrom(event.getActor()),
                RepoDTO.convertFrom(event.getRepo()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(
                                new Date(
                                        event.getCreatedAt().getTime()
                                )
                        )
        );
    }
}
