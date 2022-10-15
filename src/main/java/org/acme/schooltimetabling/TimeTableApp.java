package org.acme.schooltimetabling;

import org.acme.schooltimetabling.data.TimeTableIO;
import org.acme.schooltimetabling.domain.*;
import org.acme.schooltimetabling.solver.TimeTableConstraintProvider;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
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

        SolverFactory<TimeTable> solverFactory = SolverFactory.createFromXmlFile(new File(".//src/main/java/org/acme/schooltimetabling/solver/timeTableSolverConfig.xml"));

        // Load the problem
        TimeTable problem;
        TimeTableIO io = new TimeTableIO();
        problem = io.read(new File(".//import2.xlsx"));


       PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(
               new File(".//src/main/java/org/acme/schooltimetabling/solver/TabuBenchmarkConfig.xml"));

        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(problem);
        benchmark.benchmarkAndShowReportInBrowser();


//        Solver<TimeTable> solver = solverFactory.buildSolver();
//        TimeTable solution = solver.solve(problem);
//
//        io.write(solution, new File(".//output.xlsx"));
    }



}