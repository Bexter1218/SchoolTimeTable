package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.Lesson;
import org.acme.schooltimetabling.domain.Teacher;
import org.acme.schooltimetabling.domain.Timeslot;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import static java.lang.System.in;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                roomConflict(constraintFactory),
                teacherConflict(constraintFactory),
                studentGroupConflict(constraintFactory),
                lessonsInNotTheRightRoom(constraintFactory),
                teacherNotAvailableConflict(constraintFactory)
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
                .asConstraint("Room conflict");
    }

    private Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // A teacher can teach at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getTeacher),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict");
    }

    private Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict");
    }

    private Constraint lessonsInNotTheRightRoom(ConstraintFactory constraintFactory){
        //The lessons must be held in the preferred room
        return constraintFactory
                .forEach(Lesson.class)
                .filter((lesson) -> (lesson.getPreferredRoom() != null)&&!(lesson.getRoom().toString().equals(lesson.getPreferredRoom())))
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

}