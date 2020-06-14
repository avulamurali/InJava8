package com.hackerrank.github.controller;

import com.hackerrank.github.model.Actor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ActorTuple {
    protected Actor actor;
    protected Integer cEvents;
    protected Timestamp last;
}
