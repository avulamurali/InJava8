package com.hackerrank.github.controller;

import com.hackerrank.github.controller.dto.ActorDTO;
import com.hackerrank.github.controller.dto.EventDTO;
import com.hackerrank.github.controller.dto.RepoDTO;
import com.hackerrank.github.controller.utils.DtoUtils;
import com.hackerrank.github.model.Actor;
import com.hackerrank.github.model.Event;
import com.hackerrank.github.model.Repo;
import com.hackerrank.github.repository.ActorRepository;
import com.hackerrank.github.repository.EventRepository;
import com.hackerrank.github.repository.RepoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@RestController
public class GithubApiRestController {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final ActorRepository actorRepository;
    private final RepoRepository repoRepository;

    public GithubApiRestController(EventRepository eventRepository, ActorRepository actorRepository, RepoRepository repoRepository) {
        this.eventRepository = eventRepository;
        this.actorRepository = actorRepository;
        this.repoRepository = repoRepository;
    }

    /**
     * Erasing all the events: The service should be able to erase all the events by the DELETE request at /erase.
     * The HTTP response code should be 200.
     *
     */
    @DeleteMapping(value = "/erase")
    public ResponseEntity deleteEvents() {
        eventRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    /**
     *  simple login to avoid white label error
     * @return
     */
    @GetMapping(path="/")
    public String login() {
        System.out.println("******************logging************************");
        return "GitHub DataSet Api";
    }

    /**
     *  Adding new events: The service should be able to add a new event by the POST request at /events.
     * 	The event JSON is sent in the request body.
     * 	If an event with the same id already exists then the HTTP response code should be 400, otherwise, the response code should be 201.
     * @param body
     * @return
     */
    @PostMapping(value = "/events")
    public ResponseEntity addEvent(@RequestBody EventDTO body) {

        if (Objects.nonNull(eventRepository.findOne(body.getId()))) {
            return ResponseEntity.badRequest().build();
        }

        ActorDTO actorDTO = body.getActor();
        Actor actor = new Actor(actorDTO.getId(), actorDTO.getLogin(), actorDTO.getAvatar());
        actorRepository.save(actor);

        RepoDTO repoDTO = body.getRepo();
        Repo repo = new Repo(repoDTO.getId(), repoDTO.getName(), repoDTO.getUrl());
        repoRepository.save(repo);

        Timestamp timestamp;
        try {
            timestamp = new Timestamp(format.parse(body.getCreatedAt()).getTime());
        } catch (ParseException e) {
            timestamp = new Timestamp(Instant.now().toEpochMilli());
        }

        Event event = new Event(body.getId(), body.getType(), actor, repo, timestamp);
        eventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Returning all the events: The service should be able to return the JSON array of all the events by the GET request at /events.
     * The HTTP response code should be 200. The JSON array should be sorted in ascending order by event ID.
     *
     * @return
     */
    @GetMapping(value = "/events", produces = "application/json")
    public ResponseEntity<List<EventDTO>> getAllEvents() {

        List<Event> events = eventRepository.findAll(new Sort(Sort.Direction.ASC, "id"));

        return events.isEmpty() ?
                ResponseEntity.ok(new ArrayList<>()) :
                ResponseEntity.ok(
                        events.stream()
                                .map(EventDTO::convertFrom)
                                .collect(Collectors.toList())
                );
    }

    /**
     * Returning the event records filtered by the actor ID: The service should be able to return the JSON array of all the events
     * which are performed by the actor ID by the GET request at /events/actors/{actorID}.
     * If the requested actor does not exist then HTTP response code should be 404, otherwise,
     * the response code should be 200. The JSON array should be sorted in ascending order by event ID.
     * Updating the avatar URL of the actor: The service should be able to update the avatar URL of the actor by the PUT request at /actors.
     * The actor JSON is sent in the request body. If the actor with the id does not exist then the response code should be 404, or
     * if there are other fields being updated for the actor then the HTTP response code should be 400, otherwise, the response code should be 200.
     *
     * @param actorID
     * @return
     */
    @GetMapping(value = "/events/actors/{actorID}", produces = "application/json")
    public ResponseEntity<List<EventDTO>> getAllEventsByActorId(@PathVariable Long actorID) {
        Actor actor = actorRepository.findOne(actorID);
        if (isNull(actor)) {
            return ResponseEntity.notFound().build();
        }
        List<Event> events = eventRepository.findAllByActorIdOrderByIdAsc(actorID);

        return events.isEmpty() ?
                ResponseEntity.ok(new ArrayList<>()) :
                ResponseEntity.ok(
                        events.stream()
                                .map(EventDTO::convertFrom)
                                .collect(Collectors.toList())
                );
    }

    /**
     * Updating the avatar URL of the actor: The service should be able to update the avatar URL of the actor by the PUT request at /actors.
     * The actor JSON is sent in the request body. If the actor with the id does not exist then the response code should be 404, or if there are other
     * fields being updated for the actor then the HTTP response code should be 400, otherwise, the response code should be 200.
     * @param body
     * @return
     */
    @PutMapping(value = "/actors", produces = "application/json")
    public ResponseEntity<ActorDTO> updateActorAvatarURL(@RequestBody ActorDTO body) {
        Actor actor = actorRepository.findOne(body.getId());

        if (Objects.isNull(actor)) {
            return ResponseEntity.notFound().build();
        }
        if (!body.getLogin().equals(actor.getLogin())) {
            return ResponseEntity.badRequest().build();
        }
        actor.setAvatar(body.getAvatar());
        Actor actorUpdated = actorRepository.save(actor);
        return ResponseEntity.ok(ActorDTO.convertFrom(actorUpdated));
    }

    /**
     * Returning the actor records ordered by the total number of events: The service should be able to return the JSON array of all the actors sorted
     * by the total number of associated events with each actor in descending order by the GET request at /actors.
     * If there are more than one actors with the same number of events, then order them by the timestamp of the
     * latest event in the descending order. If more than one actors have the same timestamp for the latest event,
     * then order them by the alphabetical order of login. The HTTP response code should be 200.
     *
     * @return
     */
    @GetMapping(value = "/actors", produces = "application/json")
    public ResponseEntity<List<ActorDTO>> getActors() {
        List<Event> events = eventRepository.findAll();
        List<Actor> actors = actorRepository.findAll();

        List<ActorTuple> actorTuples = new ArrayList<>();

        for (Actor actor : actors) {
            List<Event> collect = events.stream()
                    .filter(event -> event.getActor().equals(actor))
                    .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            if (!collect.isEmpty()) {
                actorTuples.add(new ActorTuple(actor, collect.size(), collect.get(0).getCreatedAt()));
            }
        }

        List<ActorDTO> actorList = DtoUtils.getCollectionWithCriteria(actorTuples);

        return ResponseEntity.ok(actorList);
    }

    /**
     * Returning the actor records ordered by the maximum streak: The service should be able to return the JSON array of all the actors
     * sorted by the maximum streak (i.e., the total number of consecutive days actor has pushed an event to the system)
     * in descending order by the GET request at /actors/streak. If there are more than one actors with the same maximum streak,
     * then order them by the timestamp of the latest event in the descending order.
     * If more than one actors have the same timestamp for the latest event, then order them by the alphabetical order of login.
     * The HTTP response code should be 200.
     *
     * @return
     */
    @GetMapping(value = "/actors/streak", produces = "application/json")
    public ResponseEntity<List<ActorDTO>> getActorsStreak() {
        List<Event> events = eventRepository.findAll();
        List<Actor> actors = actorRepository.findAll();

        List<ActorTuple> actorTupleStreaks = new ArrayList<>();

        for (Actor actor : actors) {
            List<Event> collect = events.stream()
                    .filter(event -> event.getActor().equals(actor) && event.getType().equals("PushEvent"))
                    .sorted(Comparator.comparing(Event::getCreatedAt).reversed())
                    .collect(Collectors.toList());

            if (!collect.isEmpty()) {
                if (collect.size() == 1) {
                    actorTupleStreaks.add(new ActorTuple(actor, 0, collect.get(0).getCreatedAt()));
                } else {
                    Integer mayorStreak = DtoUtils.getStreak(collect);
                    actorTupleStreaks.add(new ActorTuple(actor, mayorStreak, collect.get(0).getCreatedAt()));
                }
            }
        }
        List<ActorDTO> actorList = DtoUtils.getCollectionWithCriteria(actorTupleStreaks);
        return ResponseEntity.ok(actorList);
    }

}
