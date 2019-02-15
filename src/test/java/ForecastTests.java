import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.testng.Assert;
import org.testng.annotations.*;
import Forecast.Common;
import Forecast.Elastic_RForecast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ForecastTests {
    private String es_host = "";
    private int es_port = 9200;
    private String Rscriptslocation = "C:/NewTimeSeriesFunctions.R";
    private Elastic_RForecast rForecast;

    private String metricName = "bugsratiojira";
    private String indexMetrics = "metrics.*";
    private String tsFrequency = "7";
    private String tsForecastHorizon = "10";
    private String sd;

    @BeforeClass
    public void connectR() throws REXPMismatchException, REngineException {
        rForecast = new Elastic_RForecast(es_host, es_port, "", Rscriptslocation);
        sd = rForecast.getModelsDirectory();
    }

    @Test
    public void test_training_arima() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "Arima";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.ARIMA);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_arima_force_seasonality() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "ArimaFS";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_theta() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "Theta";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.THETA);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_ets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "ETS";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.ETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_etsdamped() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "ETSFD";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.ETSDAMPED);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_baggedets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "BaggedETS";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.BAGGEDETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_stl() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "STL";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.STL);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_NN() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "NN";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.NN);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_Hybrid() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "Hybrid";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.HYBRID);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }

    @Test
    public void test_training_Prophet() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = sd + "/" + metricName + "_" + indexMetrics + "_" + "Prophet";
        Files.deleteIfExists(new File(pathToFile).toPath());

        rForecast.trainForecastModel(metricName, indexMetrics, tsFrequency, Common.ForecastTechnique.PROPHET);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
    }



    @AfterClass
    public void close() {
        rForecast.close();
    }

}
