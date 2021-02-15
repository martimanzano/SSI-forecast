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
        String model_file = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA.toString();
        Files.deleteIfExists(new File(model_file).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.ARIMA);
        Assert.assertTrue(Files.exists(new File(model_file).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_arima() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.ARIMA);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_arima_force_seasonality() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA_FORCE_SEASONALITY.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA_FORCE_SEASONALITY.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.ARIMA_FORCE_SEASONALITY);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_arima_force_seasonality() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ARIMA_FORCE_SEASONALITY.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.ARIMA_FORCE_SEASONALITY);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_theta() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.THETA.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.THETA.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.THETA);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_theta() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.THETA.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.THETA);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_ets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETS.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETS.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.ETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_ets() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETS.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.ETS);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_etsdamped() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETSDAMPED.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETSDAMPED.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.ETSDAMPED);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_etsdamped() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.ETSDAMPED.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.ETSDAMPED);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_baggedets() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.BAGGEDETS.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.BAGGEDETS.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.BAGGEDETS);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_baggedets() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.BAGGEDETS.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.BAGGEDETS);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_stl() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.MSTLM.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.MSTLM.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.MSTLM);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_stl() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.MSTLM.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.MSTLM);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_NN() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.NN.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.NN.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.NN);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_NN() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.NN.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.NN);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_Hybrid() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.HYBRID.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.HYBRID.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.HYBRID);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_Hybrid() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.HYBRID.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.HYBRID);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_training_Prophet() throws REXPMismatchException, REngineException, IOException {
        String pathToFile = models_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.PROPHET.toString();
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.PROPHET.toString();
        Files.deleteIfExists(new File(pathToFile).toPath());
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.trainForecastModel(element_name, index, tsFrequency, Common.ForecastMethod.PROPHET);
        Assert.assertTrue(Files.exists(new File(pathToFile).toPath()));
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @Test
    public void test_forecasting_Prophet() throws REXPMismatchException, REngineException, IOException {
        String forecast_file = for_cache_dir + "/" + element_name + "_" + index + "_" + Common.ForecastMethod.PROPHET.toString();
        Files.deleteIfExists(new File(forecast_file).toPath());

        rForecast.forecast(element_name, index, tsFrequency, tsForecastHorizon, Common.ForecastMethod.PROPHET);
        Assert.assertTrue(Files.exists(new File(forecast_file).toPath()));
    }

    @AfterClass
    public void close() {
        rForecast.close();
    }

}
