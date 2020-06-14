package com.hackerrank.github.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Actor {

    @Id
    private Long id;
    private String login;
    private String avatar;
}
