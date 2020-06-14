package com.hackerrank.github.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    private Long id;
    private String type;
    @OneToOne(cascade = CascadeType.ALL)
    private Actor actor;
    @OneToOne(cascade = CascadeType.ALL)
    private Repo repo;
    private Timestamp createdAt;
}
