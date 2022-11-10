package org.acme.schooltimetabling;

import org.acme.schooltimetabling.data.TimeTableIO;
import org.acme.schooltimetabling.domain.*;
import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.core.api.score.ScoreExplanation;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;


public class TimeTableApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeTableApp.class);

    public static void main(String[] args) {

        //Create the solver from Xml
        SolverFactory<TimeTable> solverFactory = SolverFactory.createFromXmlFile(
                new File(".//src/main/java/org/acme/schooltimetabling/solver/timeTableSolverConfig.xml"));

        // Load the problem
        TimeTable problem;
        TimeTableIO io = new TimeTableIO();
        problem = io.read(new File(".//import2.xlsx"));


//        //Benchmarking
//       PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlFile(
//               new File(".//src/main/java/org/acme/schooltimetabling/solver/TabuBenchmarkConfig.xml"));
//
//        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark(problem);
//        benchmark.benchmarkAndShowReportInBrowser();

        //Solving the problem
        Solver<TimeTable> solver = solverFactory.buildSolver();
        TimeTable solution = solver.solve(problem);

        //Score Explanation
        ScoreManager<TimeTable, HardSoftScore> scoreManager = ScoreManager.create(solverFactory);
        ScoreExplanation<TimeTable, HardSoftScore> scoreExplanation = scoreManager.explainScore(solution);
        System.out.println(scoreManager.getSummary(solution));

        //write the file
        io.write(solution, new File(".//output.xlsx"));
    }
}