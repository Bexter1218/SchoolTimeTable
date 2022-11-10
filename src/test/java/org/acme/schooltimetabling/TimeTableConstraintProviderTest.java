package org.acme.schooltimetabling;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.acme.schooltimetabling.domain.*;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

class TimeTableConstraintProviderTest {

    private static final Room ROOM1 = getRoom1();


    private static final Timeslot TIMESLOTM4 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 55), LocalTime.of(11, 40));
    private static final Timeslot TIMESLOTM5 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(11, 50), LocalTime.of(12, 35));
    private static final Timeslot TIMESLOTM6 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(12, 45), LocalTime.of(13, 30));
    private static final Timeslot TIMESLOTM7 = new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 40), LocalTime.of(14, 25));

    private static final Timeslot TIMESLOTW4 = new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 55), LocalTime.of(11, 40));
    private static final Timeslot TIMESLOTW5 = new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(11, 50), LocalTime.of(12, 35));
    private static final Timeslot TIMESLOTW6 = new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(12, 45), LocalTime.of(13, 30));
    private static final Timeslot TIMESLOTW7 = new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 40), LocalTime.of(14, 25));


    ConstraintVerifier<TimeTableConstraintProvider, TimeTable> constraintVerifier = ConstraintVerifier.build(
            new TimeTableConstraintProvider(), TimeTable.class, Lesson.class);

    @Test
    void roomConflict() {
        long id = 0;
        Lesson firstLesson = new Lesson(id++, "Subject1", "Teacher1", "Group1");
        firstLesson.setRoom(ROOM1);
        firstLesson.setTimeslot(TIMESLOTM4);

        Lesson conflictingLesson = new Lesson(id++, "Subject2", "Teacher2", "Group2");
        conflictingLesson.setRoom(ROOM1);
        conflictingLesson.setTimeslot(TIMESLOTM4);

        Lesson nonConflictingLesson = new Lesson(id++, "Subject3", "Teacher3", "Group3");
        nonConflictingLesson.setRoom(ROOM1);
        nonConflictingLesson.setTimeslot(TIMESLOTM5);

        constraintVerifier.verifyThat(TimeTableConstraintProvider::roomConflict)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }

    @Test
    void roomConflict2() {
        long id = 0;
        Lesson firstLesson = new Lesson(id++, "Subject1", "Teacher1", "Group1");
        firstLesson.setRoom2(ROOM1);
        firstLesson.setTimeslot(TIMESLOTM4);

        Lesson conflictingLesson = new Lesson(id++, "Subject2", "Teacher2", "Group2");
        conflictingLesson.setRoom2(ROOM1);
        conflictingLesson.setTimeslot(TIMESLOTM4);

        Lesson nonConflictingLesson = new Lesson(id++, "Subject3", "Teacher3", "Group3");
        nonConflictingLesson.setRoom2(ROOM1);
        nonConflictingLesson.setTimeslot(TIMESLOTM5);

        constraintVerifier.verifyThat(TimeTableConstraintProvider::roomConflict2)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }

    @Test
    void roomConflict3() {
        long id = 0;
        Lesson firstLesson = new Lesson(id++, "Subject1", "Teacher1", "Group1");
        firstLesson.setRoom(ROOM1);
        firstLesson.setTimeslot(TIMESLOTM4);

        Lesson conflictingLesson = new Lesson(id++, "Subject2", "Teacher2", "Group2");
        conflictingLesson.setRoom2(ROOM1);
        conflictingLesson.setTimeslot(TIMESLOTM4);

        Lesson nonConflictingLesson = new Lesson(id++, "Subject3", "Teacher3", "Group3");
        nonConflictingLesson.setRoom(ROOM1);
        nonConflictingLesson.setTimeslot(TIMESLOTM5);

        constraintVerifier.verifyThat(TimeTableConstraintProvider::roomConflict3)
                .given(firstLesson, conflictingLesson, nonConflictingLesson)
                .penalizesBy(1);
    }





    private static Room getRoom1(){
        List<String> roomSubjects = new ArrayList<>();
        roomSubjects.add("Matek");
        return new Room("Room1", roomSubjects);
    }
}
