package Forecast;

import Assessment.DTOSIAssessment;
import Assessment.SIAssessment;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Elastic_RForecast {
    private final String es_host;
    private final int es_port;
    private final String es_path;
    private final String es_user;
    private final String es_pwd;
    private final String rserve_host;
    private final int rserve_port;
    private RConnection rconnection = null;
    private final String Rfunctions_path;

    /**
     * Connects with RServe and sources the script's functions to R
     * @param es_host: IP or domain name of the elasticsearch instance containing the data
     * @param es_port: Port of the elasticsearch instance containing the data
     * @param es_path: (Optional, "" if there is no path) Path to the elasticsearch instance
     * @param es_user: (to use if the elasticsearch is protected with basic auth.) Username to access the elasticsearch instance
     * @param es_pwd: (to use if the elasticsearch is protected with basic auth.) Password to access the elasticsearch instance
     * @param Rfunctions_path: (On Rserve side) Path to the R scripts' file
     * @throws REngineException
     * @throws REXPMismatchException
     */
    public Elastic_RForecast(String es_host, int es_port, String es_path, String es_user, String es_pwd, String Rfunctions_path)
            throws REngineException, REXPMismatchException {
        this.es_host = es_host;
        this.es_port = es_port;
        this.es_path = es_path;
        this.es_user = es_user;
        this.es_pwd = es_pwd;
        this.rserve_host = "127.0.0.1";
        this.rserve_port = 6311;
        this.Rfunctions_path = Rfunctions_path;

        this.initializeR();
    }

    /**
     * Connects with RServe and sources the script's functions to R
     * @param es_host: IP or domain name of the elasticsearch instance containing the data
     * @param es_port: Port of the elasticsearch instance containing the data
     * @param es_path: (Optional, "" if there is no path) Path to the elasticsearch instance
     * @param es_user: (to use if the elasticsearch is protected with basic auth.) Username to access the elasticsearch instance
     * @param es_pwd: (to use if the elasticsearch is protected with basic auth.) Password to access the elasticsearch instance
     * @param rserve_host: IP or domain name of RServe. Default is localhost
     * @param rserve_port: Port to use to access RServe. Default is 6311
     * @param Rfunctions_path: (On Rserve side) Path to the R scripts' file
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public Elastic_RForecast(String es_host, int es_port, String es_path, String es_user, String es_pwd,
                             String rserve_host, int rserve_port, String Rfunctions_path)
            throws REXPMismatchException, REngineException {
        this.es_host = es_host;
        this.es_port = es_port;
        this.es_path = es_path;
        this.es_user = es_user;
        this.es_pwd = es_pwd;
        this.rserve_host = rserve_host;
        this.rserve_port = rserve_port;
        this.Rfunctions_path = Rfunctions_path;

        this.initializeR();
    }

    /**
     *
     * Connects with RServe and sources the script's functions to R
     * @param es_host: IP or domain name of the elasticsearch instance containing the data
     * @param es_port: Port of the elasticsearch instance containing the data
     * @param es_path: (Optional, "" if there is no path) Path to the elasticsearch instance
     * @param Rfunctions_path: (On Rserve side) Path to the R scripts' file
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public Elastic_RForecast(String es_host, int es_port, String es_path, String Rfunctions_path)
            throws REXPMismatchException, REngineException {
        this.es_host = es_host;
        this.es_port = es_port;
        this.es_path = es_path;
        this.es_user = "";
        this.es_pwd = "";
        this.rserve_host = "127.0.0.1";
        this.rserve_port = 6311;
        this.Rfunctions_path = Rfunctions_path;

        this.initializeR();
    }

    /**
     * Connects with RServe and sources the script's functions to R
     * @param es_host: IP or domain name of the elasticsearch instance containing the data
     * @param es_port: Port of the elasticsearch instance containing the data
     * @param es_path: (Optional, "" if there is no path) Path to the elasticsearch instance
     * @param rserve_host: IP or domain name of RServe. Default is localhost
     * @param rserve_port: Port to use to access RServe. Default is 6311
     * @param Rfunctions_path: (On Rserve side) Path to the R scripts' file
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public Elastic_RForecast(String es_host, int es_port, String es_path, String rserve_host, int rserve_port, String Rfunctions_path)
            throws REXPMismatchException, REngineException {
        this.es_host = es_host;
        this.es_port = es_port;
        this.es_path = es_path;
        this.es_user = "";
        this.es_pwd = "";
        this.rserve_host = rserve_host;
        this.rserve_port = rserve_port;
        this.Rfunctions_path = Rfunctions_path;

        this.initializeR();
    }

    public RConnection getRconnection() {return this.rconnection;}

    private void initializeR() throws REXPMismatchException, REngineException {
        this.rconnection = new RConnection(this.rserve_host, this.rserve_port);
        sourceTimeSeriesFunctions();
        initESConnection();
    }

    /**
     * Closes the connection to RServe
     */
    public void close() {
        this.rconnection.close();
    }

    public String getModelsDirectory() throws REXPMismatchException, REngineException {
        return Common.getModelsDirectory(this.rconnection);
    }

    public String getForecastsCacheDirectory() throws REXPMismatchException, REngineException {
        return Common.getForecastsCacheDirectory(this.rconnection);
    }

    private void sourceTimeSeriesFunctions() throws REXPMismatchException, REngineException {
        //SOURCE TIME SERIES FUNCTIONS
        sourceFile(this.Rfunctions_path);
    }

    public void sourceFile(String path) throws REXPMismatchException, REngineException {
        String sourceFileStatement = "source(\"" + path + "\")";
        Common.evaluateR(this.rconnection, sourceFileStatement);
        System.out.println(path + " SOURCED");
    }

    private void initESConnection() throws REXPMismatchException, REngineException {
        String elasticSearchConnection = "elasticConnection(host = \"" + this.es_host + "\"," +
                " path = \"" + this.es_path + "\"," +
                " user = \"" + this.es_user + "\"," +
                " pwd = \"" + this.es_pwd + "\"," +
                " port = " + String.valueOf(this.es_port) + ")";

        Common.evaluateR(this.rconnection, elasticSearchConnection);
        System.out.println("ELASTIC SEARCH CONNECTION SUCCESSFUL");// + rResponseObject.asString());
    }

    /**
     * Get the names of the forecasting techniques available
     * @return Common.ForecastingTechnique[]
     */
    public Common.ForecastMethod[] getForecastTechniques () throws REXPMismatchException, REngineException {
        String scriptAvailableMethods[] = Common.getAvailableMethods(this.rconnection);
        Common.ForecastMethod availableToReturn[] = new Common.ForecastMethod[scriptAvailableMethods.length];
        for (int i = 0; i < scriptAvailableMethods.length; i++) {
            availableToReturn[i] = Common.ForecastMethod.valueOf(scriptAvailableMethods[i]);
        }
        return availableToReturn;
    }

    public boolean[] testAutocorrelation(String elementNames[], String index, String[] tsFrequency, String dateFrom,
                                         String dateTo)
            throws REXPMismatchException, REngineException {
        boolean[] haveAutocorrelation = new boolean[elementNames.length];
        for (int i = 0; i < elementNames.length; i++) {
            Common.evaluateR(this.getRconnection(), "dateFrom <<- \"" + dateFrom + "\"");
            Common.evaluateR(this.getRconnection(), "dateTo <<- \"" + dateTo + "\"");

            String autocorrTestCall = "autoCorrelationTestWrapper(elementName = \"" + elementNames[i] + "\", " +
                    "index = \"" + index + "\", " + "tsFrequency = " + tsFrequency[i] + ")";

            haveAutocorrelation[i] = Boolean.parseBoolean(Common.evaluateR(this.rconnection, autocorrTestCall).asString());
        }
        return haveAutocorrelation;
    }

    /**
     * Fits multiple forecast models using a single forecasting technique and saves them to disk. This function should
     * be called periodically in order to keep the saved models up-to-date with recent data.
     * @param elementNames: array containing the elements for which the forecasting models will be fitted
     * @param index: elasticsearch index containing the elements of "elementNames" array
     * @param tsFrequency: The value of argument frequency is used when the series is sampled an integral number
     *                  of times in each unit time interval. For example, one could use a value of 7 for frequency
     *                  when the data are sampled daily, and the natural time period is a week, or 12 when the data
     *                  are sampled monthly and the natural time period is a year. Values of 4 and 12 are assumed in
     *                  (e.g.) print methods to imply a quarterly and monthly series respectively.
     *                  Source: "ts" R documentation
     * @param technique: Technique to be used to fit the forecasting model. To be defined using the enum
     *                 "Common.ForecastTechnique"
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public String[] multipleElementTrain(String elementNames[], String index, String tsFrequency,
                                                          Common.ForecastMethod technique)
            throws REXPMismatchException, REngineException {
        String trainResult[] = new String[elementNames.length];

        for (int i = 0; i < elementNames.length; i++) {
            try {
                trainForecastModel(elementNames[i], index, tsFrequency, technique);
                trainResult[i] = "OK";
            } catch (ArithmeticException e) {
                trainResult[i] = e.getMessage();
            }
        }
        return trainResult;
    }

    /**
     * Forecasts multiple models using a single forecasting technique. For every element, the method will check if there
     * exists a saved fitted model with the chosed technique. If it's found, it will be loaded and used to perform the
     * forecast. If not, the model will be fitted and saved to disk prior to the forecasting.
     * @param elementNames: array containing the elements for which the forecasting models will be fitted
     * @param index: elasticsearch index containing the elements of "elementNames" array
     * @param tsFrequency: The value of argument frequency is used when the series is sampled an integral number
     *                  of times in each unit time interval. For example, one could use a value of 7 for frequency
     *                  when the data are sampled daily, and the natural time period is a week, or 12 when the data
     *                  are sampled monthly and the natural time period is a year. Values of 4 and 12 are assumed in
     *                  (e.g.) print methods to imply a quarterly and monthly series respectively.
     *                  Source: "ts" R documentation
     * @param horizon: Number of periods to forecast. If working with daily data, it will correspond to the number
     *                  of days to forecast
     * @param technique: Technique to be used to fit the forecasting model. To be defined using the enum
     *                 "Common.ForecastTechnique"
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public ArrayList<ForecastDTO> multipleElementForecast(String elementNames[], String index, String tsFrequency,
                                                       String horizon, Common.ForecastMethod technique)
            throws REXPMismatchException, REngineException {
        ArrayList<ForecastDTO> forecasts = new ArrayList<>();
        for (String elementName : elementNames) {
            forecasts.add(forecast(elementName, index, tsFrequency, horizon, technique));
        }
        return forecasts;
    }

    public ForecastDTO[] multipleElementForecast(String elementNames[], String index, String[] tsFrequency,
                                                          String horizon, Common.ForecastMethod[] technique,
                                                          String dateTrainFrom, String dateTrainTo)
            throws REXPMismatchException, REngineException {
        ForecastDTO[] forecasts = new ForecastDTO[elementNames.length];
        for (int i = 0; i < elementNames.length; i++) {
            Common.evaluateR(this.getRconnection(), "dateFrom <<- \"" + dateTrainFrom + "\"");
            Common.evaluateR(this.getRconnection(), "dateTo <<- \"" + dateTrainTo + "\"");
            forecasts[i] = forecast(elementNames[i], index, tsFrequency[i], horizon, technique[i]);
        }
        return forecasts;
    }

    public DTOSIAssessment[] forecastSI(String[] elementNames, String[] tsFrequencies,
                                        Common.ForecastMethod[] technique, String index, String horizon,
                                        String IDSI, double[][] intervals_per_element, String dateTrainFrom,
                                        String dateTrainTo, File BNFile)
            throws Exception {
        // First step: forecast numeric values
        ForecastDTO[] forecasts = multipleElementForecast(elementNames, index, tsFrequencies, horizon,
                technique, dateTrainFrom, dateTrainTo);

        // Second step: Feed the BN with the forecasted values and needed parameters
        System.out.println("FORECASTED VALUES TO FEED THE BN : \n" + forecasts.toString());
        DTOSIAssessment[] forecastedAssessments = new DTOSIAssessment[Integer.parseInt(horizon)];
        for (int i = 0; i < forecastedAssessments.length; i++) {
            forecastedAssessments[i] = SIAssessment.AssessSI(IDSI, elementNames, Utils.buildDayForecastedValues(forecasts, i),
                    intervals_per_element, BNFile);
            System.out.println("Horizon: " + i + "\n Numeric values: " + Arrays.toString(Utils.buildDayForecastedValues(forecasts, i)));
        }
        return forecastedAssessments;
    }

    /**
     * Fits an individual forecasting model using the selected forecasting technique and saves the model to disk.
     * This function should be called periodically in order to keep the saved model up-to-date with recent data.
     * @param elementName: Element for which the forecasting model will be fitted
     * @param index: elasticsearch index containing the element to forecast ("elementName")
     * @param tsFrequency: The value of argument frequency is used when the series is sampled an integral number
     *                  of times in each unit time interval. For example, one could use a value of 7 for frequency
     *                  when the data are sampled daily, and the natural time period is a week, or 12 when the data
     *                  are sampled monthly and the natural time period is a year. Values of 4 and 12 are assumed in
     *                  (e.g.) print methods to imply a quarterly and monthly series respectively.
     *                  Source: "ts" R documentation
     * @param technique: Technique to be used to fit the forecasting model. To be defined using the enum
     *                 "Common.ForecastTechnique"
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public void trainForecastModel(String elementName, String index, String tsFrequency,
                                 Common.ForecastMethod technique) throws REXPMismatchException, REngineException {
        switch (technique) {
                case ARIMA:
                    System.out.println("FITTING ARIMA MODEL");
                    trainArimaModel(elementName, index, "FALSE", tsFrequency);
                    break;
                case ARIMA_FORCE_SEASONALITY:
                    System.out.println("FITTING ARIMA MODEL, FORCING SEASONALITY");
                    trainArimaModel(elementName, index, "TRUE", tsFrequency);
                    break;
                case THETA:
                    System.out.println("FITTING THETA MODEL");
                    trainThetaModel(elementName, index, tsFrequency);
                    break;
                case ETS:
                    System.out.println("FITTING ETS MODEL");
                    trainETSModel(elementName, index, "FALSE", tsFrequency);
                    break;
                case ETSDAMPED:
                    System.out.println("FITTING ETS DAMPED MODEL");
                    trainETSModel(elementName, index, "TRUE", tsFrequency);
                    break;
                case BAGGEDETS:
                    System.out.println("FITTING BAGGED ETS MODEL (THIS CAN TAKE A WHILE)");
                    trainBaggedETSModel(elementName, index, tsFrequency);
                    break;
                case MSTLM:
                    System.out.println("FITTING STL MODEL");
                    trainSTLModel(elementName, index, tsFrequency);
                    break;
            case TBATS:
                    System.out.println("FITTING TBATS MODEL");
                    trainTBATSModel(elementName, index, tsFrequency);
                    break;
                case NN:
                    System.out.println("FITTING NEURAL NETWORK (NNETA) MODEL");
                    trainNNModel(elementName, index, tsFrequency);
                    break;
                case HYBRID:
                    System.out.println("FITTING HYBRID MODEL (THIS CAN TAKE A WHILE)");
                    trainHybridModel(elementName, index, "10", tsFrequency);
                    break;
                default:
                case PROPHET:
                    System.out.println("FITTING PROPHET MODEL");
                    trainProphetModel(elementName, index);
                break;
        }
    }

    /**
     * Forecasts using the selected forecasting technique. The method will check if there exists a saved fitted model
     * with the chosed technique. If it's found, it will be loaded and used to perform the forecast. If not, the model
     * will be fitted and saved to disk prior to the forecasting.
     * @param elementName: Element for which the forecasting model will be fitted
     * @param index: elasticsearch index containing the element to forecast ("elementName")
     * @param tsFrequency: The value of argument frequency is used when the series is sampled an integral number
     *                  of times in each unit time interval. For example, one could use a value of 7 for frequency
     *                  when the data are sampled daily, and the natural time period is a week, or 12 when the data
     *                  are sampled monthly and the natural time period is a year. Values of 4 and 12 are assumed in
     *                  (e.g.) print methods to imply a quarterly and monthly series respectively.
     *                  Source: "ts" R documentation
     * @param horizon: Number of periods to forecast. If working with daily data, it will correspond to the number
     *                  of days to forecast
     * @param technique: Technique to be used to fit the forecasting model. To be defined using the enum
     *                 "Common.ForecastTechnique"
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public ForecastDTO forecast(String elementName, String index, String tsFrequency, String horizon,
                                      Common.ForecastMethod technique)
            throws REXPMismatchException, REngineException {
        switch (technique) {
            case ARIMA:
                System.out.println("FORECASTING WITH ARIMA MODEL");
                return forecastArimaModel(elementName, index, "FALSE", tsFrequency, horizon);
            case ARIMA_FORCE_SEASONALITY:
                System.out.println("FORECASTING WITH ARIMA MODEL, FORCING SEASONALITY");
                return forecastArimaModel(elementName, index, "TRUE", tsFrequency, horizon);
            case THETA:
                System.out.println("FORECASTING WITH THETA MODEL");
                return forecastThetaModel(elementName, index, tsFrequency, horizon);
            case ETS:
                System.out.println("FORECASTING WITH ETS MODEL");
                return forecastETSModel(elementName, index, "FALSE", tsFrequency, horizon);
            case ETSDAMPED:
                System.out.println("FORECASTING WITH ETS DAMPED MODEL");
                return forecastETSModel(elementName, index, "TRUE", tsFrequency, horizon);
            case BAGGEDETS:
                System.out.println("FORECASTING WITH BAGGED ETS MODEL");
                return forecastBaggedETSModel(elementName, index, tsFrequency, horizon);
            case MSTLM:
                System.out.println("FORECASTING WITH STL MODEL");
                return forecastSTLModel(elementName, index, tsFrequency, horizon);
            case TBATS:
                System.out.println("FORECASTING WITH TBATS MODEL");
                return forecastTBATSModel(elementName, index, tsFrequency, horizon);
            case NN:
                System.out.println("FORECASTING WITH NEURAL NETWORK (NNETA) MODEL (THIS CAN TAKE A WHILE)");
                return forecastNNModel(elementName, index, tsFrequency, horizon);
            case HYBRID:
                System.out.println("FORECASTING WITH HYBRID MODEL");
                return forecastHybridModel(elementName, index, tsFrequency, horizon);
            default:
            case PROPHET:
                System.out.println("FORECASTING WITH PROPHET MODEL");
                return forecastProphetModel(elementName, index, horizon);
        }
    }

    private void trainArimaModel(String elementName, String index, String forceSeasonality, String frequencyts)
            throws REXPMismatchException, REngineException {
        String arimaTrainCall = "trainArimaModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "forceSeasonality = " + forceSeasonality + ", frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, arimaTrainCall);
    }

    private ForecastDTO forecastArimaModel(String elementName, String index, String forceSeasonality,
                                           String frequencyts, String tsForecastHorizon)
            throws REXPMismatchException, REngineException {
        String arimaForecastCall = "forecastArimaWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "forceSeasonality = " + forceSeasonality + ", frequencyts = " + frequencyts + ", " +
                "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, arimaForecastCall, elementName);
    }

    private void trainThetaModel(String elementName, String index, String frequencyts)
            throws REXPMismatchException, REngineException {
        String thetaTrainCall = "trainThetaModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, thetaTrainCall);
    }

    private ForecastDTO forecastThetaModel(String elementName, String index, String frequencyts,
                                           String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String thetaForecastCall = "forecastThetaWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, thetaForecastCall, elementName);
    }

    private void trainETSModel(String elementName, String index, String forceDamped, String frequencyts)
            throws REXPMismatchException, REngineException {
        String etsTrainCall = "trainETSModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "forceDamped = " + forceDamped + ", frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, etsTrainCall);
    }

    private ForecastDTO forecastETSModel(String elementName, String index, String forceDamped,
                                           String frequencyts, String tsForecastHorizon)
            throws REXPMismatchException, REngineException {
        String etsForecastCall = "forecastETSWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "forceDamped = " + forceDamped + ", frequencyts = " + frequencyts + ", " +
                "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, etsForecastCall, elementName);
    }

    private void trainBaggedETSModel(String elementName, String index, String frequencyts)
            throws REXPMismatchException, REngineException {
        String baggedETSTrainCall = "trainBaggedETSModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, baggedETSTrainCall);
    }

    private ForecastDTO forecastBaggedETSModel(String elementName, String index, String frequencyts,
                                           String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String baggedETSForecastCall = "forecastBaggedETSWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, baggedETSForecastCall, elementName);
    }

    private void trainSTLModel(String elementName, String index, String frequencyts)
            throws REXPMismatchException, REngineException {
        String STLModelTrainCall = "trainSTLModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, STLModelTrainCall);
    }

    private ForecastDTO forecastSTLModel(String elementName, String index, String frequencyts,
                                               String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String STLforecastCall = "forecastSTLWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, STLforecastCall, elementName);
    }

    private void trainTBATSModel(String elementName, String index, String frequencyts)
            throws REXPMismatchException, REngineException {
        String TBATSModelTrainCall = "trainTBATSModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, TBATSModelTrainCall);
    }

    private ForecastDTO forecastTBATSModel(String elementName, String index, String frequencyts,
                                         String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String TBATSForecastCall = "forecastTBATSWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, TBATSForecastCall, elementName);
    }

    private void trainNNModel(String elementName, String index, String frequencyts)
            throws REXPMismatchException, REngineException {
        String NNModelTrainCall = "trainNNModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, NNModelTrainCall);
    }

    private ForecastDTO forecastNNModel(String elementName, String index, String frequencyts,
                                         String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String NNForecastCall = "forecastNNWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, NNForecastCall, elementName);
    }

    private void trainHybridModel(String elementName, String index, String cvHorizon, String frequencyts)
            throws REXPMismatchException, REngineException {
        String hybridTrainCall = "trainHybridModel(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "cvHorizon = " + cvHorizon + ", frequencyts = " + frequencyts + ")";

        Common.evaluateR(this.rconnection, hybridTrainCall);
    }

    private ForecastDTO forecastHybridModel(String elementName, String index, String frequencyts,
                                        String tsForecastHorizon)  throws REXPMismatchException, REngineException {
        String hybridForecastCall = "forecastHybridWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "frequencyts = " + frequencyts + ", " + "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, hybridForecastCall, elementName);
    }

    private void trainProphetModel(String elementName, String index)
            throws REXPMismatchException, REngineException {
        String prophetModelTrainCall = "trainProphetModel(name = \"" + elementName + "\", " + "index = \"" + index + "\")";

        Common.evaluateR(this.rconnection, prophetModelTrainCall);
    }

    private ForecastDTO forecastProphetModel(String elementName, String index, String tsForecastHorizon)
            throws REXPMismatchException, REngineException {
        String prophetForecastCall = "forecastProphetWrapper(name = \"" + elementName + "\", " + "index = \"" + index + "\", " +
                "horizon = " + tsForecastHorizon + ")";

        return Common.evaluateRforecast(this.rconnection, prophetForecastCall, elementName);
    }
}