package com.hackerrank.github.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackerrank.github.model.Actor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActorDTO {
    private Long id;
    private String login;
    @JsonProperty(value = "avatar_url")
    private String avatar;

    public static ActorDTO convertFrom(Actor actor) {
        return new ActorDTO(actor.getId(), actor.getLogin(), actor.getAvatar());
    }
}
