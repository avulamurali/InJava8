package com.hackerrank.github.repository;

import com.hackerrank.github.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByActorIdOrderByIdAsc( Long actorId );
}
