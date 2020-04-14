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
    private String es_host = "localhost";
    private int es_port = 9200;
    private String Rscriptslocation = "C:/TimeSeriesFunctions_GPL_0.5.1.R";
    private Elastic_RForecast rForecast;

    private String element_name = "blocking";
    private String index = "strategic_indicators.demo";
    private String tsFrequency = "7";
    private String tsForecastHorizon = "20";
    private String models_dir;
    private String for_cache_dir;

    @BeforeClass
    public void connectR() throws REXPMismatchException, REngineException {
        rForecast = new Elastic_RForecast(es_host, es_port, "", Rscriptslocation);
        models_dir = rForecast.getModelsDirectory();
        for_cache_dir = rForecast.getForecastsCacheDirectory();
    }

    @Test
    public void test_training_arima() throws REXPMismatchException, REngineException, IOException {
        String model_file = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA.toString();
        Files.deleteIfExists(new File(model_file).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.ARIMA);
        Assert.assertTrue(Files.exists(new File(model_file).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_arima() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.ARIMA);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_arima_force_seasonality() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_arima_force_seasonality() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.ARIMA_FORCE_SEASONALITY);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_theta() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.THETA.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.THETA.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.THETA);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_theta() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.THETA.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.THETA);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_ets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETS.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETS.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.ETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_ets() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETS.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.ETS);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_etsdamped() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETSDAMPED.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETSDAMPED.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.ETSDAMPED);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_etsdamped() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.ETSDAMPED.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.ETSDAMPED);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_baggedets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.BAGGEDETS.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.BAGGEDETS.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.BAGGEDETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_baggedets() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.BAGGEDETS.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.BAGGEDETS);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_stl() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.STL.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.STL.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.STL);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_stl() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.STL.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.STL);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_NN() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.NN.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.NN.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.NN);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_NN() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.NN.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.NN);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_Hybrid() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.HYBRID.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.HYBRID.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.HYBRID);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_Hybrid() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.HYBRID.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.HYBRID);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_Prophet() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.PROPHET.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.PROPHET.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastTechnique.PROPHET);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_Prophet() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastTechnique.PROPHET.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastTechnique.PROPHET);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @AfterClass
    public void close() {
        rForecast.close();
    }

}
