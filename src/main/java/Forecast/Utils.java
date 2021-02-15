package Forecast;
import Assessment.DTOSIAssessment;
import DTOs.MetricEvaluationDTO;
import Util_Assessment_SI.BayesUtils;
import Util_Assessment_SI.Constants;
import evaluation.Metric;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;

public class Utils {

    public static MetricEvaluationDTO[] getMetricEvaluations(String projectId, String[] metricIDs, LocalDate from,
                                                             LocalDate to) throws IOException {
        MetricEvaluationDTO[] ret = new MetricEvaluationDTO[metricIDs.length];
        for (int i = 0; i < metricIDs.length; i++) {
            ret[i] = Metric.getSingleEvaluation(projectId, metricIDs[i], from, to);
            if (ret[i] != null && ret[i].getEvaluations().size() > 0) {
                Collections.reverse(ret[i].getEvaluations());
            }
        }
        return ret;
    }

    public static double[] computeCategoriesAccuracy(ForecastDTO forecastedMetric, MetricEvaluationDTO observedMetric,
                                                   double[] intervals, String[] categories, LocalDate dateFrom,
                                                   int horizon) {
        int coincidences = 0;
        double RMSE = 0;

        if (observedMetric != null && forecastedMetric.getMean() != null) {
            int observedMetricsIndex = 0, forecastIndex;
            LocalDate firstDayWithEvaluation = observedMetric.getEvaluations().get(0).getEvaluationDate();
            forecastIndex = (int) dateFrom.until(firstDayWithEvaluation, ChronoUnit.DAYS);
        /*while (dateFrom.isBefore(observedMetric.getEvaluations().get(observedMetricsIndex).getEvaluationDate())) {
            forecastIndex++;
        }*/
            LocalDate dateIndexObserved = firstDayWithEvaluation;
            while (forecastIndex < horizon && observedMetricsIndex < observedMetric.getEvaluations().size()) {
                if (dateIndexObserved.equals(observedMetric.getEvaluations().get(observedMetricsIndex).getEvaluationDate())) {
                    RMSE += Math.pow(observedMetric.getEvaluations().get(observedMetricsIndex).getValue() -
                            forecastedMetric.getMean()[forecastIndex], 2);

                    String forecastedCategory = BayesUtils.discretize(forecastedMetric.getMean()[forecastIndex], intervals, categories);
                    String observedCategory = BayesUtils.discretize(observedMetric.getEvaluations().get(observedMetricsIndex).getValue(), intervals, categories);

                    if (forecastedCategory.equals(observedCategory)) coincidences++;
                }

                forecastIndex++;
                observedMetricsIndex++;
                dateIndexObserved = dateIndexObserved.plusDays(1);
            }
        }
        double acc = (double) coincidences/horizon;
        double RMSEtotal = (double) Math.sqrt(RMSE/horizon);
        double[] ret = new double[]{acc, RMSEtotal};

        return ret;
    }

    static double[] buildDayForecastedValues(ForecastDTO[] forecasts, int indexDay) {
        double[] ret = new double[forecasts.length];
        for (int i = 0; i < forecasts.length; i++) {
            ForecastDTO forecast = forecasts[i];
            if (!forecast.hasError()) {
                ret[i] = forecasts[i].getMean()[indexDay];
            } else {
                ret[i] = Double.NaN;
            }
        }
        return ret;
    }

    public static Common.ForecastMethod benchmarkForecastingMethods(Elastic_RForecast rForecast, String elementName,
                                                                    String tsFrequency, String index, String horizon,
                                                                    String dateTrainFrom, String dateTrainTo,
                                                                    String error_index, String benchmarkScriptPath)
            throws REXPMismatchException, REngineException {

        rForecast.sourceFile(benchmarkScriptPath);
        //benchmarkForecastingMethods <- function(elementName, index, frequency, dateTrainFrom, dateTrainTo, horizon, error_index) {
        String benchmarkMethodsRCall = "benchmarkForecastingMethods(" +
                "elementName = \"" + elementName + "\", " +
                "index = \"" + index + "\", " +
                "frequency = " + tsFrequency + ", " +
                "dateTrainFrom = \"" + dateTrainFrom + "\", " +
                "dateTrainTo = \"" + dateTrainTo + "\", " +
                "horizon = " + horizon + ", " +
                "error_index = " + error_index + ")";
        System.out.println("Validating CV parameters...");
        REXP bestMethodResponse = Common.evaluateR(rForecast.getRconnection(), benchmarkMethodsRCall);
        return Common.ForecastMethod.valueOf(bestMethodResponse.asString());

    }

    public static boolean validateCVbenchmarkData(Elastic_RForecast rForecast, String[] elementNames, String tsFrequency,
                                                  String index, String dateFrom,
                                                  String dateTo, String maxHorizonCV,
                                                  String windowSizeCVPercentage)
            throws REXPMismatchException, REngineException {

        Common.evaluateR(rForecast.getRconnection(), "dateFrom <<- \"" + dateFrom + "\"");
        Common.evaluateR(rForecast.getRconnection(), "dateTo <<- \"" + dateTo + "\"");
        String elementNamesRvar = "elementNames";
        rForecast.getRconnection().assign(elementNamesRvar, elementNames);

        String CVbenchmarkValidationRCall = "validateCVInput(" +
                "elementNames = " + elementNamesRvar + ", " +
                "index = \"" + index + "\", " +
                "tsFrequency = " + tsFrequency + ", " +
                "windowSizeCVPercent = " + windowSizeCVPercentage + ", " +
                "maxHorizonCV = " + maxHorizonCV + ")";

        REXP CVDatavalidationResponse = Common.evaluateR(rForecast.getRconnection(), CVbenchmarkValidationRCall);

        return CVDatavalidationResponse.asString().equals("TRUE");
    }

    public static Common.ForecastMethod CVbenchmarkForecastingMethods(Elastic_RForecast rForecast, String elementName,
                                                                      String tsFrequency, String index, String dateFrom,
                                                                      String dateTo, String maxHorizonCV,
                                                                      String windowSizeCVPercentage, String error_index,
                                                                      String CVbenchmarkScriptPath)
            throws REXPMismatchException, REngineException {

        rForecast.sourceFile(CVbenchmarkScriptPath);
        String CVbenchmarkMethodsRCall = "benchmarkCVForecastingMethods(" +
                "elementName = \"" + elementName + "\", " +
                "index = \"" + index + "\", " +
                "frequency = " + tsFrequency + ", " +
                "dateFrom = \"" + dateFrom + "\", " +
                "dateTo = \"" + dateTo + "\", " +
                "maxHorizonCV = " + maxHorizonCV + ", " +
                "windowSizeCVPercent = " + windowSizeCVPercentage + ", " +
                "error_index = " + error_index + ")";
        System.out.println("Performing TS Cross-Validation for " + elementName + "...This may take a while.");
        REXP bestMethodResponse = Common.evaluateR(rForecast.getRconnection(), CVbenchmarkMethodsRCall);
        return Common.ForecastMethod.valueOf(bestMethodResponse.asString());
    }

    public static void forecastSIandReport(Elastic_RForecast rForecast, String[] elementNames, String[] tsFrequencies,
                                           Common.ForecastMethod[] techniques, String index, String horizon,
                                           String IDSI, String[][] categories_per_element, String[] child_categories,
                                           double[][] intervals_per_element, String dateTrainFrom, String dateTrainTo,
                                           File BNFile, FileWriter csvWriter)
            throws Exception {
        // USED VARIABLES
        LocalDate dateTrainFrom_ld = LocalDate.parse(dateTrainFrom);
        LocalDate dateTrainTo_ld = LocalDate.parse(dateTrainTo);
        LocalDate dateForecastFrom = dateTrainTo_ld.plusDays(1);
        LocalDate dateForecastTo = dateTrainTo_ld.plusDays(Integer.parseInt(horizon));
        Constants.QMLevel qmLevel = Constants.QMLevel.valueOf(index.split("\\.")[0].toLowerCase());
        String projectID = index.split("\\.")[1];

        // COMPUTE FORECASTS
        // SI
        DTOSIAssessment[] forecastedAssessments = rForecast.forecastSI(elementNames, tsFrequencies, techniques, index, horizon,
                IDSI, intervals_per_element, dateTrainFrom, dateTrainTo, BNFile);
        // METRICS
        ForecastDTO[] forecastedMetrics = rForecast.multipleElementForecast(elementNames, index, tsFrequencies, horizon,
                techniques, dateForecastFrom.toString(), dateForecastTo.toString());
        MetricEvaluationDTO[] metricEvaluations = getMetricEvaluations(projectID, elementNames, dateForecastFrom,
                dateForecastTo);

        // COMPUTE ASSESSMENTS
        DTOSIAssessment[] observedAssessments = BayesUtils.computeAndReturnChildStates(projectID, qmLevel, elementNames,
                categories_per_element, intervals_per_element, IDSI, BNFile, dateTrainFrom_ld, dateForecastTo);
                                                                            //dateForecastFrom
        // PREPARE CSV HEADINGS
        csvWriter.append("DAY,, OBSERVED CATEGORIES' PROBS.,,,,,, FORECASTED CATEGORIES' PROBS.,,,,, COINCIDE/DISTANCE\n");
        csvWriter.append("Day,").append(String.join(",", child_categories)).append(",Highest Prob.,,").
                append(String.join(",", child_categories)).append(",Higuest Prob.,,Coincide,Distance\n");

        // WRITE ASSESSMENTS PART OF TRAINING DATA
        int iObserved = 0;
        for (LocalDate indexDay = dateTrainFrom_ld; !indexDay.isAfter(dateTrainTo_ld); indexDay = indexDay.plusDays(1)) {
            String row = new StringBuilder().append(indexDay).append(",").
                    append(String.join(",", observedAssessments[iObserved].buildDayProbabilities())).append(",").
                    append(",(training)".repeat(5)).append(",").append(",(training)".repeat(2)).toString();
            csvWriter.append(row);
            csvWriter.append("\n");
            iObserved++;
        }

        // WRITE ASSESSMENTS-FORECASTS
        int iForecasted = 0, coincidences = 0;
        for (LocalDate indexDay = dateForecastFrom; !indexDay.isAfter(dateForecastTo); indexDay = indexDay.plusDays(1)) {
            DTOSIAssessment obsAssessment = observedAssessments[iObserved];
            DTOSIAssessment forcAssessment = forecastedAssessments[iForecasted];
            Boolean coincide = obsAssessment.isEqualAssessment(forcAssessment);
            if (coincide) coincidences++;

            String row = new StringBuilder().append(indexDay).append(",").
                    append(String.join(",", obsAssessment.buildDayProbabilities())).append(",,").
                    append(String.join(",", forcAssessment.buildDayProbabilities())).append(",,").
                    append(coincide.toString().toUpperCase()).append(",").
                    append(obsAssessment.computeDistanceTo(forcAssessment)).toString();
            csvWriter.append(row);
            csvWriter.append("\n");
            iForecasted++;
            iObserved++;
        }

        // WRITE SUMMARY
        StringBuilder summary = new StringBuilder(new StringBuilder().
                append("SIZE TOTAL:,").append(iObserved).
                append("\nSIZE TRAINING:,").append(dateTrainFrom_ld.until(dateTrainTo_ld, ChronoUnit.DAYS) + 1).
                append("\nSIZE TEST:,").append(horizon).
                append("\nTEST COINC.:,").append(coincidences).
                append("\nTEST ACCUR.:,").append((float) coincidences / Integer.parseInt(horizon) * 100).append("%").
                append("\nAVG DISTANCE:,") + String.valueOf(BayesUtils.computeAverageDistance(
                Arrays.copyOfRange(observedAssessments,
                        (int) (dateTrainFrom_ld.until(dateTrainTo_ld, ChronoUnit.DAYS) + 1),
                        observedAssessments.length), forecastedAssessments)));

        // METRICS' SUMMARY
        double accAccuracyOfMetrics = 0, RMSEofMetrics = 0;
        for (int i = 0; i < forecastedMetrics.length; i++) {
            String IDofIndividualMetric = forecastedMetrics[i].getId();
            double[] accuracyAndRMSEofIndividualMetric = computeCategoriesAccuracy(forecastedMetrics[i],
                    metricEvaluations[i], intervals_per_element[i], categories_per_element[i], dateForecastFrom,
                    Integer.parseInt(horizon));

            summary.append("\n").append(IDofIndividualMetric, 0, 7).append(".").
                    append(" ACC./RMSE:,").append((float) accuracyAndRMSEofIndividualMetric[0] * 100).append("%,").
                    append((float) accuracyAndRMSEofIndividualMetric[1]);

            accAccuracyOfMetrics += accuracyAndRMSEofIndividualMetric[0];
            RMSEofMetrics += accuracyAndRMSEofIndividualMetric[1];
        }
        summary.append("\n").append("AVG METRICS TEST ACC./RMSE:,").append(
                (float) (accAccuracyOfMetrics/forecastedMetrics.length) * 100).append("%,").
                append((float) (RMSEofMetrics/forecastedMetrics.length));

        csvWriter.append(summary.toString());
    }

    public static void printMethodsMatrix(Common.ForecastMethod[][] computedBestMethodsPerExperimentAndElement) {
        for (int i = 0; i < computedBestMethodsPerExperimentAndElement.length; i++) {
            for (int j = 0; j < computedBestMethodsPerExperimentAndElement[i].length; j++) {
                System.out.print(computedBestMethodsPerExperimentAndElement[i][j] + " ");
            }
            System.out.println();
        }
    }
}
