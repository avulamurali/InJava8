package com.hackerrank.github.controller.utils;

import com.hackerrank.github.controller.ActorTuple;
import com.hackerrank.github.controller.dto.ActorDTO;
import com.hackerrank.github.model.Event;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DtoUtils {

    public static int getStreak(List<Event> collect) {
        int mayorStreak = 0;
        int streak = 0;
        for (int i = collect.size() - 1; i > 0; i--) {
            LocalDateTime currentDate = collect.get(i).getCreatedAt().toLocalDateTime();
            LocalDateTime nextDate = collect.get(i - 1).getCreatedAt().toLocalDateTime();
            LocalDateTime currentDateEndOfDay = currentDate.with(ChronoField.NANO_OF_DAY, LocalTime.MAX.toNanoOfDay());

            long hours = ChronoUnit.HOURS.between(currentDate, nextDate);
            long hoursFinalDay = ChronoUnit.HOURS.between(currentDate, currentDateEndOfDay);
            long days = ChronoUnit.DAYS.between(currentDate, nextDate);

            if (currentDate.getDayOfMonth() == nextDate.getDayOfMonth() || days > 1) {
                streak = 0;
            } else if (hours - hoursFinalDay <= 24) {
                streak++;
                if (streak > mayorStreak)
                    mayorStreak = streak;
            }
        }
        return mayorStreak;
    }

    public static List<ActorDTO> getCollectionWithCriteria(List<ActorTuple> actorTuples) {
        return actorTuples.stream()
                .sorted(Comparator.comparing(o -> o.getActor().getLogin()))
                .sorted(Comparator.comparing(ActorTuple::getLast).reversed())
                .sorted(Comparator.comparing(ActorTuple::getCEvents).reversed())
                .map(ActorTuple::getActor)
                .map(ActorDTO::convertFrom)
                .collect(Collectors.toList());
    }
}
