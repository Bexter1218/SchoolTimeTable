package org.acme.schooltimetabling.solver;

import org.acme.schooltimetabling.domain.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.Duration;
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
                studentEmptyLessonConflict(constraintFactory),
                morePePerDay(constraintFactory),


//                 Soft constraints are only implemented in the OptaPlanner-quickstarts code
                lunchBreakConflict(constraintFactory),
                homeWorkLessonDuringDayConflict(constraintFactory),
                headTeacherFistLessonWithStudentGroup(constraintFactory),
                skillBasedSubjectsEarlyConflict(constraintFactory),
                peAsFirstLessonOnMondayAndFridayConflict(constraintFactory),
                peAfterLunch(constraintFactory),
                headTeacherClassNotOnFriday(constraintFactory),
                connectedLessonsSeperated(constraintFactory),
                sameSubjectSameDay(constraintFactory)

        };
    }

    public Constraint roomConflict(ConstraintFactory constraintFactory) {
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
                .filter(((lesson, lesson2) -> {
                    if(lesson.getTimeslot().getStartTime().getHour() > 12 && lesson.getSubject().equals("Testnevelés") && lesson2.getSubject().equals("Testnevelés"))
                        return false;
                    return true;
                }))
                // ... then penalize each pair with a hard weight.
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 1-1");
    }

    public Constraint roomConflict2(ConstraintFactory constraintFactory) {

        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.lessThan(Lesson::getId))
                .filter(((lesson, lesson2) -> {
                    if(lesson.getTimeslot().getStartTime().getHour() > 12 && lesson.getSubject().equals("Testnevelés") && lesson2.getSubject().equals("Testnevelés"))
                        return false;
                    if(lesson.getRoom2() == null || lesson2.getRoom2() == null)
                        return false;
                    return lesson.getRoom2().equals(lesson2.getRoom2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 2-2");
    }

    public Constraint roomConflict3(ConstraintFactory constraintFactory) {

        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot))
                .filter(((lesson, lesson2) -> {
                    if(lesson.getTimeslot().getStartTime().getHour() > 12 && lesson.getSubject().equals("Testnevelés") && lesson2.getSubject().equals("Testnevelés"))
                        return false;
                    if(lesson2.getRoom2() == null)
                        return false;
                    return lesson.getRoom().equals(lesson2.getRoom2());
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict 1-2");
    }

    public Constraint teacherConflict(ConstraintFactory constraintFactory) {
        // A teacher can teach at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getTeacher),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher conflict 1-1");
    }

    public Constraint teacherConflict2(ConstraintFactory constraintFactory) {
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

    public Constraint teacherConflict3(ConstraintFactory constraintFactory) {
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

    public Constraint studentGroupConflict(ConstraintFactory constraintFactory) {
        // A student can attend at most one lesson at the same time.
        return constraintFactory.forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTimeslot),
                        Joiners.equal(Lesson::getStudentGroup),
                        Joiners.lessThan(Lesson::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Student group conflict 1-1");
    }

    public Constraint studentGroupConflict2(ConstraintFactory constraintFactory) {
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

    public Constraint studentGroupConflict3(ConstraintFactory constraintFactory) {
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

    public Constraint lessonsInNotTheRightRoom(ConstraintFactory constraintFactory){
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
                .penalize(HardSoftScore.ofSoft(50))
                .asConstraint("Lesson not in the right room");
    }


    public Constraint teacherNotAvailableConflict(ConstraintFactory constraintFactory){
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

    public Constraint teacherTooMuchInSchoolTheSameDay(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getTeacher))
                .filter(((lesson, lesson2) -> {
                    Duration between = Duration.between(lesson.getTimeslot().getEndTime(),
                            lesson2.getTimeslot().getStartTime());
                    return !between.isNegative() && between.compareTo(Duration.ofHours(8)) > 0;
                }))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Too much in school the same day");
    }

    public Constraint studentEmptyLessonConflict(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Lesson.class)
                .ifNotExists(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.filtering((lesson1, lesson2) -> {
                            if(hasTheSameStudentGroup(lesson1, lesson2))
                            {
                                Duration between = Duration.between(lesson2.getTimeslot().getEndTime(),
                                        lesson1.getTimeslot().getStartTime());
                                return !between.isNegative() && between.compareTo(Duration.ofMinutes(30)) < 0;
                            }
                            return false;
                        }))
                .filter(lesson -> lesson.getTimeslot().startTime().getHour() != 7)
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Empty Lesson for a student group");
    }

    public Constraint morePePerDay(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.equal(Lesson::getSubject),
                        Joiners.lessThan(Lesson::getId),
                        Joiners.filtering(this::hasTheSameStudentGroup))
                .filter((lesson, lesson2) -> lesson.getSubject().equals("Testnevelés"))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("More than one PE lesson on the same day for one student group");
    }

    public Constraint lunchBreakConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter((lesson) -> {
                    if(!(lesson.getSubject().equals("Ebéd")))
                        return false;
                    if(lessonInLowerGrade(lesson))
                        return lesson.getTimeslot().getStartTime().getHour() != 12;
                    else
                        return lesson.getTimeslot().getStartTime().getHour() != 13;
                })
                .penalize(HardSoftScore.ofSoft(20))
                .asConstraint("The students can only eat during the given timespan");
    }

    public Constraint homeWorkLessonDuringDayConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.filtering(this::hasTheSameStudentGroup))
                .filter((lesson1, lesson2) -> {
                    if(lesson1.getSubject().equals("Napközi")){
                        if(lesson2.getTimeslot().getStartTime().getHour() > lesson1.getTimeslot().getStartTime().getHour())
                            return !(lesson2.getSubject().equals("Napközi"));
                        if(lesson1.getTimeslot().getStartTime().getHour() < 12)
                            return true;
                    }
                    return false;
                })
                .penalize(HardSoftScore.ofSoft(50))
                .asConstraint("Homework lessons should be held in the end of each day");
    }

    public Constraint headTeacherFistLessonWithStudentGroup(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(StudentGroup.class,
                        Joiners.equal(Lesson::getStudentGroup, StudentGroup::getName))
                .filter((lesson, studentGroup)-> {
                    if(!(lesson.getTimeslot().getStartTime().equals(LocalTime.of(7, 55))))
                        return false;
                    return !(lesson.getTeacher().equals(studentGroup.headTeacher()));
                })
                .penalize(HardSoftScore.ofSoft(5))
                .asConstraint("First Lesson not with Head Teacher");
    }

    public Constraint skillBasedSubjectsEarlyConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Subject.class,
                        Joiners.equal(Lesson::getSubject, Subject::getName))
                .filter(((lesson, subject) -> {
                    if(subject.isAbilityBased())
                        return false;
                    return lesson.getTimeslot().startTime().getHour() < 11;
                }))
                .penalize(HardSoftScore.ofSoft(3))
                .asConstraint(("Skill based subject in the first 4 Lesson of the day"));

    }

    public Constraint peAsFirstLessonOnMondayAndFridayConflict(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> {
                    if(lesson.getTimeslot().getStartTime().getHour() == 7 && lesson.getSubject().equals("Testnevelés"))
                        if(lesson.getTimeslot().getDayOfWeek().equals(DayOfWeek.MONDAY) || lesson.getTimeslot().getDayOfWeek().equals(DayOfWeek.FRIDAY))
                            return true;
                    return false;
                })
                .penalize(HardSoftScore.ofSoft(3))
                .asConstraint("PE class as fist lesson on monday or friday");
    }

    public Constraint peAfterLunch(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.lessThan(Lesson::getId))
                .filter((lesson1, lesson2) -> {
                    if(hasTheSameStudentGroup(lesson1, lesson2))
                        if(lesson1.getSubject().equals("Ebéd"))
                            if(lesson2.getTimeslot().getStartTime().getHour() == lesson1.getTimeslot().getStartTime().getHour() + 1)
                                return !(lesson2.getSubject().equals("Testnevelés"));
                    return false;
                })
                .penalize(HardSoftScore.ofSoft(1))
                .asConstraint("PE class after lunch");
    }

    public Constraint headTeacherClassNotOnFriday(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .filter(lesson -> {
                    if(lesson.getSubject().equals("Osztályfőnöki"))
                        return !lesson.getTimeslot().getDayOfWeek().equals(DayOfWeek.FRIDAY);
                    return false;
                })
                .penalize(HardSoftScore.ofSoft(10))
                .asConstraint("Hometeacher class not on Friday");
    }

    public Constraint connectedLessonsSeperated(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.lessThan(Lesson::getId))
                .filter((lesson1, lesson2) -> {
                    if(!hasTheSameStudentGroup(lesson1,lesson2))
                        return false;
                    if(lesson1.getPairingID().equals("") && lesson2.getPairingID().equals(""))
                        return false;
                    if(lesson2.getTimeslot().getStartTime().getHour() == lesson1.getTimeslot().getStartTime().getHour() +1)
                        return !lesson2.getPairingID().equals(lesson1.getPairingID());
                    return false;
                })
                .penalize(HardSoftScore.ofSoft(5))
                .asConstraint("Connected lessons separated");
    }

    public Constraint sameSubjectSameDay(ConstraintFactory constraintFactory){
        return constraintFactory
                .forEach(Lesson.class)
                .join(Lesson.class,
                        Joiners.equal(Lesson::getDayOfWeek),
                        Joiners.lessThan(Lesson::getId))
                .filter((lesson1, lesson2) -> {
                    if(!hasTheSameStudentGroup(lesson1,lesson2))
                        return false;
                    return lesson1.getSubject().equals(lesson2.getSubject());
                })
                .penalize(HardSoftScore.ofSoft(1))
                .asConstraint("Same Lesson on the same day");
    }


//    private Constraint skillAndKnowledgeBalanceConflict(ConstraintFactory constraintFactory) {
//        return constraintFactory
//                .forEach(Lesson.class)
//                .join(Subject.class,
//                        Joiners.equal(Lesson::getSubject, Subject::getName))
//                .filter(((lesson, subject) -> {
//                    if(subject.isAbilityBased())
//                        return false;
//                    return false;
//                }))
//                .groupBy()
//                .groupBy(Lesson::getStudentGroup, count())
//                .
//
//    }


    private boolean lessonBetweenTheTwo(Lesson lesson1, Lesson lesson2){
        return true;
    }

    private boolean lessonInLowerGrade(Lesson lesson) {
        return lesson.getStudentGroup().equals("1. osztály")
                || lesson.getStudentGroup().equals("2. osztály")
                || lesson.getStudentGroup().equals("3. osztály")
                || lesson.getStudentGroup().equals("4. osztály");
    }


    private boolean hasTheSameStudentGroup(Lesson lesson1, Lesson lesson2){
        if(lesson1.getStudentGroup2().equals("")){
            if(lesson2.getStudentGroup2().equals(""))
                return lesson1.getStudentGroup().equals(lesson2.getStudentGroup());
            else
                return lesson1.getStudentGroup().equals(lesson2.getStudentGroup())
                    || lesson1.getStudentGroup().equals(lesson2.getStudentGroup2());
        }
        else{
            if(lesson2.getStudentGroup2().equals(""))
                return lesson1.getStudentGroup().equals(lesson2.getStudentGroup())
                    || lesson1.getStudentGroup2().equals(lesson2.getStudentGroup());
            else
                return lesson1.getStudentGroup().equals(lesson2.getStudentGroup())
                    || lesson1.getStudentGroup().equals(lesson2.getStudentGroup2())
                    || lesson1.getStudentGroup2().equals(lesson2.getStudentGroup())
                    || lesson1.getStudentGroup2().equals(lesson2.getStudentGroup2());
        }
    }


}