package org.acme.schooltimetabling;

import org.acme.schooltimetabling.domain.*;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
                .withEntityClasses(Lesson.class, SplitLesson.class)
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




//        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromSolverConfigXmlResource(
//                "schooltimetabling/solver/timeTableSolverConfig.xml");
//
//        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(problem);
//        benchmark.benchmarkAndShowReportInBrowser();

        // Solve the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        exportTimetable(solution);
        // Visualize the solution
        // printTimetable(solution);
    }


    public static TimeTable generateDemoData() throws IOException, DataFormatException {

        //The timeslots are not likely to change so hard coding them is acceptable
        List<Timeslot> timeslotList = new ArrayList<>(10);
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(7, 55), LocalTime.of(8, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(8, 55), LocalTime.of(9, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(9, 55), LocalTime.of(10, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(10, 55), LocalTime.of(11, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(11, 50), LocalTime.of(12, 35)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(12, 45), LocalTime.of(13, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(13, 40), LocalTime.of(14, 25)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(14, 30), LocalTime.of(15, 15)));
        timeslotList.add(new Timeslot(DayOfWeek.MONDAY, LocalTime.of(15, 15), LocalTime.of(16, 0)));

        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(7, 55), LocalTime.of(8, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(8, 55), LocalTime.of(9, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(9, 55), LocalTime.of(10, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(10, 55), LocalTime.of(11, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(11, 50), LocalTime.of(12, 35)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(12, 45), LocalTime.of(13, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(13, 40), LocalTime.of(14, 25)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(14, 30), LocalTime.of(15, 15)));
        timeslotList.add(new Timeslot(DayOfWeek.TUESDAY, LocalTime.of(15, 15), LocalTime.of(16, 0)));

        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(7, 55), LocalTime.of(8, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(8, 55), LocalTime.of(9, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(9, 55), LocalTime.of(10, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(10, 55), LocalTime.of(11, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(11, 50), LocalTime.of(12, 35)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(12, 45), LocalTime.of(13, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 40), LocalTime.of(14, 25)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(14, 30), LocalTime.of(15, 15)));
        timeslotList.add(new Timeslot(DayOfWeek.WEDNESDAY, LocalTime.of(15, 15), LocalTime.of(16, 0)));

        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(7, 55), LocalTime.of(8, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(8, 55), LocalTime.of(9, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(9, 55), LocalTime.of(10, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(10, 55), LocalTime.of(11, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(11, 50), LocalTime.of(12, 35)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(12, 45), LocalTime.of(13, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(13, 40), LocalTime.of(14, 25)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(14, 30), LocalTime.of(15, 15)));
        timeslotList.add(new Timeslot(DayOfWeek.THURSDAY, LocalTime.of(15, 15), LocalTime.of(16, 0)));

        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(7, 55), LocalTime.of(8, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(8, 55), LocalTime.of(9, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(9, 55), LocalTime.of(10, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(10, 55), LocalTime.of(11, 40)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(11, 50), LocalTime.of(12, 35)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(12, 45), LocalTime.of(13, 30)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(13, 40), LocalTime.of(14, 25)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(14, 30), LocalTime.of(15, 15)));
        timeslotList.add(new Timeslot(DayOfWeek.FRIDAY, LocalTime.of(15, 15), LocalTime.of(16, 0)));

        //opening the import file
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "import.xlsx";
        FileInputStream file = new FileInputStream(fileLocation);
        Workbook workbook = new XSSFWorkbook(file);

        //importing the subjects
        List<Subject> subjectList = new ArrayList<>();
        Sheet subjectSheet = workbook.getSheetAt(0);
        Iterator<Row> itrS = subjectSheet.iterator();
        itrS.next();   //skipping one header row
        while (itrS.hasNext()){
            Row row = itrS.next();
            String name = row.getCell(0).getStringCellValue();
            boolean abilityBased = row.getCell(1).getBooleanCellValue();
            subjectList.add(new Subject(name,abilityBased));
        }

        //importing the rooms
        List<Room> roomList = new ArrayList<>();
        Sheet roomSheet = workbook.getSheetAt(1);
        Iterator<Row> itrR = roomSheet.iterator();
        itrR.next();
        while (itrR.hasNext()){
            List<String> roomSubjects = new ArrayList<>();
            Row row = itrR.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            String name = row.getCell(0).getStringCellValue();
            cellIterator.next();
            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                String subject = cell.getStringCellValue();
                if(notSubject(subject, subjectList))    //if the subject is unknown, it could be impossible to apply the constraints
                    throw new DataFormatException("\nNincs ilyen tantárgy!\tÓrák/"+(row.getRowNum()+1)+".sor");
                roomSubjects.add(subject);

            }
            roomList.add(new Room(name, roomSubjects));
        }

        //importing the teachers
        List<Teacher> teacherList = new ArrayList<>();
        Sheet teacherSheet = workbook.getSheetAt(2);
        Iterator<Row> itrT = teacherSheet.iterator();    //iterating the sheet
        itrT.next();    //skipping the first two rows, because these are headers
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

        //importing the student groups
        List<StudentGroup> studentGroupList =new ArrayList<>();
        Sheet studentGroupSheet = workbook.getSheetAt(3);
        Iterator<Row> itrSG = studentGroupSheet.iterator();
        itrSG.next();   //skipping one header row
        while(itrSG.hasNext()){
            Row row = itrSG.next();
            String name = row.getCell(0).getStringCellValue();
            String headTeacher = row.getCell(1).getStringCellValue();
            if(notTeacher(headTeacher, teacherList))
                throw new DataFormatException("\nNincs ilyen tanár!\tOsztályok/"+(row.getRowNum()+1)+".sor");
            studentGroupList.add(new StudentGroup(name, headTeacher));
        }

        //importing the lessons
        List<Lesson> lessonList = new ArrayList<>();
        Sheet lessonSheet = workbook.getSheetAt(4);
        Iterator<Row> itrL = lessonSheet.iterator();    //iterating over the sheet
        itrL.next();   //skipping one header row
        long id = 0;
        while (itrL.hasNext()) {
            Row row = itrL.next();
            String subject = row.getCell(0).getStringCellValue();
            if(notSubject(subject, subjectList))    //if the subject is unknown, it could be impossible to apply the constraints
                throw new DataFormatException("\nNincs ilyen tantárgy!\tÓrák/"+(row.getRowNum()+1)+".sor");
            String teacher = row.getCell(1).getStringCellValue();
            if(notTeacher(teacher, teacherList))    //if the teacher is unknown, it could be impossible to apply the constraints
                throw new DataFormatException("\nNincs ilyen tanár!\tÓrák/"+(row.getRowNum()+1)+".sor");
            String studentGroup = row.getCell(2).getStringCellValue();
            if(notStudentGroup(studentGroup, studentGroupList)) //if the student group is unknown, it could be hard to apply the constraints
                throw new DataFormatException("\nNincs ilyen osztály!\tÓrák/"+(row.getRowNum()+1)+".sor");
            Cell cell = row.getCell(3);
            if(cell != null){
                String teacher2 = cell.getStringCellValue();
                lessonList.add(new SplitLesson(id++, subject, teacher, teacher2, studentGroup));
            }
            else
                lessonList.add(new Lesson(id++, subject, teacher, studentGroup));
        }
        file.close();
        //creating the new timetable with the given data
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

    private static void exportTimetable(TimeTable timeTable) {
        Workbook workbook = new XSSFWorkbook();


        Sheet wholeSheet = workbook.createSheet("Teljes órarend");
        int wholeRowNumber = 0;
        //wholeHeader
        Row wholeHeader = wholeSheet.createRow(wholeRowNumber++);
        int headerCellNumber = 0;
        Cell corner = wholeHeader.createCell(headerCellNumber++, CellType.STRING);
        corner.setCellValue("Tanár");
        for(Timeslot t : timeTable.getTimeslotList()){
            Cell cell = wholeHeader.createCell(headerCellNumber++, CellType.STRING);
            cell.setCellValue(t.toString());
        }


        CellStyle cs = workbook.createCellStyle();
        cs.setWrapText(true);
        for(Teacher teacher : timeTable.getTeacherList()){
            Row row = wholeSheet.createRow(wholeRowNumber++);
            int cellNumber = 0;
            Cell nameCell = row.createCell(cellNumber++, CellType.STRING);
            nameCell.setCellValue(teacher.getName());
            for(Timeslot timeslot : timeTable.getTimeslotList()){
                Cell cell = row.createCell(cellNumber++, CellType.STRING);
                Lesson lesson = findLessonByTimeslotAndTeacher(timeTable.getLessonList(), timeslot, teacher);
                String content = "";
                if(lesson != null){
                    content = lesson.getStudentGroup().substring(0,3) + "\n"
                            + lesson.getSubject().substring(0,2) + "\n"
                            + lesson.getRoom(teacher.getName()).toString().charAt(0);
                    cell.setCellStyle(cs);
                }
                cell.setCellValue(content);
            }
        }

        wholeSheet.setDefaultColumnWidth(3);
        wholeSheet.setColumnWidth(0, 10*256);

        //write the timetable for each student group

        for(StudentGroup studentGroup : timeTable.getStudentGroupList()){
            Sheet sheet = workbook.createSheet(studentGroup.getName());
            createSimpleTimeTableHeader(sheet);

            for(Timeslot timeslot : timeTable.getTimeslotList()){
                int column = getColumnOfTimeslot(timeslot);
                int row = getRowFromTimeslot(timeslot);

                Cell cell = sheet.getRow(row).createCell(column);
                Lesson lesson = findLessonByTimeslotAndStudentGroup(timeTable.getLessonList(), timeslot, studentGroup);
                String content = "";
                if(lesson != null)
                    content = lesson.getSubject();
                cell.setCellValue(content);
            }

            sheet.setDefaultColumnWidth(10);
            sheet.setColumnWidth(0,512);
        }

        //write the timetable for each teacher

        for(Teacher teacher : timeTable.getTeacherList()){
            Sheet sheet = workbook.createSheet(teacher.getName());
            createSimpleTimeTableHeader(sheet);

            for(Timeslot timeslot : timeTable.getTimeslotList()){
                int column = getColumnOfTimeslot(timeslot);
                int row = getRowFromTimeslot(timeslot);
                Cell cell = sheet.getRow(row).createCell(column);
                Lesson lesson = findLessonByTimeslotAndTeacher(timeTable.getLessonList(), timeslot, teacher);
                String content = "";
                if(lesson != null)
                {
                    content = lesson.getSubject() + "\n" + lesson.getStudentGroup() + "\n" + lesson.getRoom();
                    cell.setCellStyle(cs);
                }
                cell.setCellValue(content);
            }
            sheet.setDefaultColumnWidth(10);
            sheet.setColumnWidth(0,512);
        }

        //saves the workbook in the outputs.xlsx file
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "output.xlsx";
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int getRowFromTimeslot(Timeslot timeslot) {
        int row = -1;
        switch (timeslot.getStartTime().getHour()){
            case 7 -> row = 1;
            case 8 -> row = 2;
            case 9 -> row = 3;
            case 10 -> row = 4;
            case 11 -> row = 5;
            case 12 -> row = 6;
            case 13 -> row = 7;
            case 14 -> row = 8;
            case 15 -> row = 9;
        }
        return row;
    }

    private static int getColumnOfTimeslot(Timeslot timeslot) {
        int column = -1;
        switch (timeslot.getDayOfWeek()){
            case MONDAY -> column = 1;
            case TUESDAY -> column = 2;
            case WEDNESDAY -> column = 3;
            case THURSDAY -> column = 4;
            case FRIDAY -> column = 5;
        }
        return column;
    }

    private static void createSimpleTimeTableHeader(Sheet sheet) {
        int rowNumber = 0;
        //header
        Row header = sheet.createRow(rowNumber++);
        header.createCell(0);
        header.createCell(1).setCellValue("Hétfő");
        header.createCell(2).setCellValue("Kedd");
        header.createCell(3).setCellValue("Szerda");
        header.createCell(4).setCellValue("Csütörtök");
        header.createCell(5).setCellValue("Péntek");
        for(int i = 1; i < 10; i++){
            Row row = sheet.createRow(rowNumber++);
            row.createCell(0).setCellValue(i);
        }
    }

    private static Lesson findLessonByTimeslotAndTeacher(List<Lesson> lessonList, Timeslot timeslot, Teacher teacher) {
        for(Lesson l : lessonList){
            if(l.getTimeslot().equals(timeslot) && (l.getTeacher().equals(teacher.getName()) || l.getTeacher2().equals(teacher.getName())))
                return l;
        }
        return null;
    }
    private static Lesson findLessonByTimeslotAndStudentGroup(List<Lesson> lessonList, Timeslot timeslot,StudentGroup studentGroup){
        for(Lesson l : lessonList){
            if(l.getTimeslot().equals(timeslot) && l.getStudentGroup().equals(studentGroup.getName()))
                return l;
        }
        return null;
    }

// --Commented out by Inspection START (2022. 10. 12. 8:25):
//    private static void printTimetable(TimeTable timeTable) {
//        LOGGER.info("");
//        List<Room> roomList = timeTable.getRoomList();
//        List<Lesson> lessonList = timeTable.getLessonList();
//        Map<Timeslot, Map<Room, List<Lesson>>> lessonMap = lessonList.stream()
//                .filter(lesson -> lesson.getTimeslot() != null && lesson.getRoom() != null)
//                .collect(Collectors.groupingBy(Lesson::getTimeslot, Collectors.groupingBy(Lesson::getRoom)));
//        LOGGER.info("|            | " + roomList.stream()
//                .map(room -> String.format("%-10s", room.getName())).collect(Collectors.joining(" | ")) + " |");
//        LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
//        for (Timeslot timeslot : timeTable.getTimeslotList()) {
//            List<List<Lesson>> cellList = roomList.stream()
//                    .map(room -> {
//                        Map<Room, List<Lesson>> byRoomMap = lessonMap.get(timeslot);
//                        if (byRoomMap == null) {
//                            return Collections.<Lesson>emptyList();
//                        }
//                        List<Lesson> cellLessonList = byRoomMap.get(room);
//                        return Objects.requireNonNullElse(cellLessonList, Collections.<Lesson>emptyList());
//                    }).toList();
//
//            LOGGER.info("| " + String.format("%-10s",
//                    timeslot.getDayOfWeek().toString().substring(0, 3) + " " + timeslot.getStartTime()) + " | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Lesson::getSubject).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
//            LOGGER.info("|            | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Lesson::getTeacher).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
//            LOGGER.info("|            | "
//                    + cellList.stream().map(cellLessonList -> String.format("%-10s",
//                            cellLessonList.stream().map(Lesson::getStudentGroup).collect(Collectors.joining(", "))))
//                    .collect(Collectors.joining(" | "))
//                    + " |");
//            LOGGER.info("|" + "------------|".repeat(roomList.size() + 1));
//        }
//        List<Lesson> unassignedLessons = lessonList.stream()
//                .filter(lesson -> lesson.getTimeslot() == null || lesson.getRoom() == null).toList();
//        if (!unassignedLessons.isEmpty()) {
//            LOGGER.info("");
//            LOGGER.info("Unassigned lessons");
//            for (Lesson lesson : unassignedLessons) {
//                LOGGER.info("  " + lesson.getSubject() + " - " + lesson.getTeacher() + " - " + lesson.getStudentGroup());
//            }
//        }
//    }
// --Commented out by Inspection STOP (2022. 10. 12. 8:25)

}