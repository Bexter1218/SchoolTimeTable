package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.time.LocalTime;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                roomConflict(constraintFactory),
                roomConflict2(constraintFactory),
                roomConflict3(constraintFactory),
                teacherConflict(constraintFactory),
                teacherConflict2(constraintFactory),
                teacherConflict3(constraintFactory),
                studentGroupConflict(constraintFactory),
                studentGroupConflict2(constraintFactory),
                studentGroupConflict3(constraintFactory),
                lessonsInNotTheRightRoom(constraintFactory),
                teacherNotAvailableConflict(constraintFactory),
                teacherTooMuchInSchoolTheSameDay(constraintFactory),
                studentEmptyLessonConflict(constraintFactory)

                // Soft constraints are only implemented in the OptaPlanner-quickstarts code

        };
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.

        // Select a lesson ...
        return constraintFactory
                .forEach(Lesson.class)
                // ... and pair it with another lesson ...
                .join(Lesson.class,
                        // ... in the same timeslot ...
                        Joiners.equal(Lesson::getTimeslot),
                        // ... in the same room ...
                        Joiners.equal(Lesson::getRoom),
                        // ... and the pair is unique (different id, no reverse pairs) ...
                        Joiners.lessThan(Lesson::getId))
                // ... then penalize each pair with a hard weight.
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 1-1");
    }

    private Constraint roomConflict2(ConstraintFactory constraintFactory) {

        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId))
                .filter(((lesson, lesson2) -> {
                    if(lesson.getRoom2() == null || lesson2.getRoom2() == null)
                        return false;
                    return lesson.getRoom2().equals(lesson2.getRoom2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 2-2");
    }

    private Constraint roomConflict3(ConstraintFactory constraintFactory) {

        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot))
                .filter(((lesson, lesson2) -> {
                    if(lesson2.getRoom2() == null)
                        return false;
                    return lesson.getRoom().equals(lesson2.getRoom2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 1-2");
    }

    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // A teacher can teach at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getTeacher),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict 1-1");
    }

    private Constraint teacherConflict2(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId))
                .filter(((lesson, lesson2) -> {
                    if(lesson2.getTeacher2().equals("") || lesson.getTeacher2().equals(""))
                        return false;
                    return lesson.getTeacher2().equals(lesson2.getTeacher2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict 2-2");
    }

    private Constraint teacherConflict3(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot))
                .filter(((lesson, lesson2) -> {
                    if(lesson2.getTeacher2().equals(""))
                        return false;
                    return lesson.getTeacher().equals(lesson2.getTeacher2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict 1-2");
    }

    private Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict 1-1");
    }

    private Constraint studentGroupConflict2(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId))
                .filter(((lesson, lesson2) -> {
                    if(lesson.getStudentGroup2().equals("") || lesson2.getStudentGroup2().equals(""))
                        return false;
                    return lesson.getStudentGroup2().equals(lesson2.getStudentGroup2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict 2-2");
    }

    private Constraint studentGroupConflict3(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot))
                .filter(((lesson, lesson2) -> {
                    if(lesson2.getStudentGroup2().equals(""))
                        return false;
                    return lesson.getStudentGroup().equals(lesson2.getStudentGroup2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict 1-2");
    }

    private Constraint lessonsInNotTheRightRoom(ConstraintFactory constraintFactory){
        //The lessons must be held in the preferred room
        return constraintFactory
                .forEach(Lesson.class)
                .filter((lesson) -> {
                    for(String s : lesson.getRoom().getTeachable()) {
                        if(s.equals(lesson.getSubject()))
                            return false;
                    }
                    return true;
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room stability");
    }


    private Constraint teacherNotAvailableConflict(ConstraintFactory constraintFactory){
        //The teacher is not available during the class
        return constraintFactory
                .forEach(Lesson.class)
                .join(Teacher.class,
                        Joiners.equal(Lesson::getTeacher, Teacher::getName))
                .filter((lesson, teacher) -> {
                    for (Timeslot t : teacher.getAvailable()
                         ) {
                        if(lesson.getTimeslot().getStartTime().equals(t.getStartTime()))
                            return false;
                    }
                    return true;
                })
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher unavailable");
    }


    private Constraint teacherTooMuchInSchoolTheSameDay(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTeacher))
                .filter(((lesson, lesson2) -> {
                    return (lesson.getTimeslot().startTime().equals(LocalTime.of(7, 55)) && lesson2.getTimeslot().getStartTime().equals(LocalTime.of(15, 15)));
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Too much in school the same day");
    }

    private Constraint studentEmptyLessonConflict(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Lesson.class)
                .ifNotExists(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.filtering((lesson1, lesson2) -> {
                            if(lesson1.getStudentGroup().equals(lesson2.getStudentGroup()) || lesson1.getStudentGroup().equals(lesson2.getStudentGroup2())
                            || lesson1.getStudentGroup2().equals(lesson2.getStudentGroup()) || lesson1.getStudentGroup2().equals(lesson2.getStudentGroup2()))
                                return lesson1.getTimeslot().getStartTime().getHour() - 1 == lesson2.getTimeslot().getStartTime().getHour();
                            return false;
                        }))
                .filter(lesson -> lesson.getTimeslot().startTime().getHour() != 7)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Empty Lesson for a student group");
    }

}