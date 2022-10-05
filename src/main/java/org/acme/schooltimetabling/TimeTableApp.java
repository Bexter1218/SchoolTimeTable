package org.acme.schooltimetabling;

import org.acme.schooltimetabling.domain.*;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class TimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);

    public static void main(String[] args) {
        SolverFactory<TimeTable> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(TimeTable.class)
                .withEntityClasses(Lesson.class)
                .withConstraintProviderClass(TimeTableConstraintProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(5)));

        // Load the problem
        TimeTable problem = null;
        try {
            problem = generateDemoData();
        } catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        // Visualize the solution
        printTimetable(solution);
    }


    public static TimeTable generateDemoData() throws IOException, DataFormatException {
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(9, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 30), LocalTime.of(10, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(11, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 30), LocalTime.of(14, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 30)));

        FileInputStream file = new FileInputStream("src\\main\\java\\org\\acme\\schooltimetabling\\import.xlsx");
        Workbook workbook = new XSSFWorkbook(file);

        List<Room> roomList = new ArrayList<>();
        Sheet roomSheet = workbook.getSheetAt(0);
        Iterator<Row> itrR = roomSheet.iterator();
        itrR.next();
        while (itrR.hasNext()){
            Row row = itrR.next();
            String name = row.getCell(0).getStringCellValue();
            roomList.add(new Room(name));
        }

        List<Teacher> teacherList = new ArrayList<>();
        Sheet teacherSheet = workbook.getSheetAt(1);
        Iterator<Row> itrT = teacherSheet.iterator();    //iterating over Excel file
        itrT.next();
        while (itrT.hasNext()) {
            Row row = itrT.next();
            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
            String name = row.getCell(0).getStringCellValue();
            List<Timeslot> available = new ArrayList<>();
            cellIterator.next();
            while(cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                if(cell.getBooleanCellValue())
                    available.add(timeslotList.get(cell.getColumnIndex() - 1));
            }
            teacherList.add(new Teacher(name, available));
        }

        List<StudentGroup> studentGroupList =new ArrayList<>();
        Sheet studentGroupSheet = workbook.getSheetAt(2);
        Iterator<Row> itrSG = studentGroupSheet.iterator();
        itrSG.next();
        while(itrSG.hasNext()){
            Row row = itrSG.next();
            String name = row.getCell(0).getStringCellValue();
            String headTeacher = row.getCell(1).getStringCellValue();
            if(notTeacher(headTeacher, teacherList))
                throw new DataFormatException("\nNincs ilyen tanár!\tOsztályok/"+(row.getRowNum()+1)+".sor");
            studentGroupList.add(new StudentGroup(name, headTeacher));
        }

        List<Subject> subjectList = new ArrayList<>();
        Sheet subjectSheet = workbook.getSheetAt(3);
        Iterator<Row> itrS = subjectSheet.iterator();
        itrS.next();
        while (itrS.hasNext()){
            Row row = itrS.next();
            String name = row.getCell(0).getStringCellValue();
            boolean abilityBased = row.getCell(1).getBooleanCellValue();
            subjectList.add(new Subject(name, abilityBased));
        }

        List<Lesson> lessonList = new ArrayList<>();
        Sheet lessonSheet = workbook.getSheetAt(4);
        Iterator<Row> itrL = lessonSheet.iterator();    //iterating over Excel file
        itrL.next();
        long id = 0;
        while (itrL.hasNext()) {
            Row row = itrL.next();
            String subject = row.getCell(0).getStringCellValue();
            if(notSubject(subject, subjectList))
                throw new DataFormatException("\nNincs ilyen tantárgy!\tÓrák/"+(row.getRowNum()+1)+".sor");
            String teacher = row.getCell(1).getStringCellValue();
            if(notTeacher(teacher, teacherList))
                throw new DataFormatException("\nNincs ilyen tanár!\tÓrák/"+(row.getRowNum()+1)+".sor");
            String studentGroup = row.getCell(2).getStringCellValue();
            if(notStudentGroup(studentGroup, studentGroupList))
                throw new DataFormatException("\nNincs ilyen osztály!\tÓrák/"+(row.getRowNum()+1)+".sor");
            try{
                String preferredRoom = row.getCell(3).getStringCellValue();
                if(notRoom(preferredRoom, roomList))
                    throw new DataFormatException("\nNincs ilyen terem!\tÓrák/"+(row.getRowNum()+1)+".sor");
                lessonList.add(new Lesson(id++, subject, teacher, studentGroup, preferredRoom));

            }
            catch (NullPointerException nullPointerException){
                lessonList.add(new Lesson(id++, subject, teacher, studentGroup));
            }
        }



        file.close();

        return new TimeTable(timeslotList, roomList, lessonList, teacherList, subjectList, studentGroupList);
    }

    private static boolean notStudentGroup(String studentGroup, List<StudentGroup> studentGroupList) {
        for(StudentGroup sg : studentGroupList)
            if(sg.getName().equals(studentGroup))
                return false;
        return true;
    }

    private static boolean notRoom(String room, List<Room> roomList) {
        for(Room r : roomList)
            if(r.getName().equals(room))
                return false;
        return true;
    }

    private static boolean notTeacher(String teacher, List<Teacher> teacherList) {
        for (Teacher t : teacherList)
            if(t.getName().equals(teacher))
                return false;
        return true;
    }

    private static boolean notSubject(String subject, List<Subject> subjectList) {
        for (Subject s : subjectList)
            if(s.getName().equals(subject))
                return false;
        return true;
    }

    private static void printTimetable(TimeTable timeTable) {
        LOGGER.info("");
        List<Room> roomList = timeTable.getRoomList();
        List<Lesson> lessonList = timeTable.getLessonList();
        Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() != null)
                .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors.groupingBy(Lesson::getRoom)));
        LOGGER.info("|            | " + roomList.stream()
                .map(room -> String.format("%-10s", room.getName())).collect(Collectors.joining(" | ")) + " |");
        LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
        for (Timeslot timeslot : timeTable.getTimeslotList()) {
            List<List<Lesson>> cellList = roomList.stream()
                    .map(room -> {
                        Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
                        if (byRoomMap == null) {
                            return Collections.<Lesson>emptyList();
                        }
                        List<Lesson> cellLessonList = byRoomMap.get(room);
                        return Objects.requireNonNullElse(cellLessonList, Collections.<Lesson>emptyList());
                    }).toList();

            LOGGER.info("| " + String.format("%-10s",
                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Lesson::getSubject).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Lesson::getTeacher).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|            | "
                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
                            cellLessonList.stream().map(Lesson::getStudentGroup).collect(Collectors.joining(", "))))
                    .collect(Collectors.joining(" | "))
                    + " |");
            LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
        }
        List<Lesson> unassignedLessons = lessonList.stream()
                .filter(lesson -> lesson.getTimeslot() == null || lesson.getRoom() == null).toList();
        if (!unassignedLessons.isEmpty()) {
            LOGGER.info("");
            LOGGER.info("Unassigned lessons");
            for (Lesson lesson : unassignedLessons) {
                LOGGER.info("  " + lesson.getSubject() + " - " + lesson.getTeacher() + " - " + lesson.getStudentGroup());
            }
        }
    }

}