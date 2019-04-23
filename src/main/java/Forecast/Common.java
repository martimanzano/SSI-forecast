package Forecast;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;

public class Common {

    /** (Extracted from R documentation)
     * ARIMA: Will fit the best ARIMA (Autoregressive integrated moving average) model according to either AIC, AICc or BIC value.     *
     * ARIMA_FORCE_SEASONALITY: Same as ARIMA but forcing seasonality
     * THETA: Theta method is equivalent to simple exponential smoothing with drift
     * ETS: Exponential smoothing state space model
     * ETSDAMPED: ETS forcing a damped trend (either additive or multiplicative)
     * BAGGEDETS: Bergmeir et al (2016) for bagging ETS forecasts
     * STL: Forecasting by Seasonal Decomposition of Time Series by Loess
     * NN: Neural Network Time Series Forecasts
     * HYBRID: Forecasting by Hybrid Models
     * PROPHET: Prophet Forecaster by Facebook
     */
    public enum ForecastTechnique {
        ARIMA, ARIMA_FORCE_SEASONALITY, THETA, ETS, ETSDAMPED, BAGGEDETS, STL, NN, HYBRID, PROPHET
    }
//    static String meanR = "$mean";
//    static String upper1 = "$upper[,1]";
//    static String upper2 = "$upper[,2]";
//    static String lower1 = "$lower[,1]";
//    static String lower = "$lower[,2]";

    static void evaluateR(RConnection rconnection, String Rcode)
            throws REXPMismatchException, REngineException {
        REXP rResponseObject = null;
        rResponseObject = rconnection.parseAndEval(
                "try(eval(" + Rcode + "))");

        if (rResponseObject.inherits("try-error")) {
            System.out.println("ERROR: R Serve Eval Exception : " + rResponseObject.asString());
            if (Rcode.toLowerCase().contains("train")) {
                throw new ArithmeticException(rResponseObject.asString()); //If there's an exception during training, we rethrow it in a controlled way
            } else {
                throw new REXPMismatchException(rResponseObject, rResponseObject.asString());
            }
        } else {
            System.out.println("EVAL OK: " + Rcode);// + rResponseObject.asString());
        }
    }

    static ForecastDTO evaluateRforecast(RConnection rconnection, String Rcode, String elementName)
            throws REngineException, REXPMismatchException {
        REXP rResponseObject = null;
        rResponseObject = rconnection.parseAndEval(
                "try(eval(" + Rcode + "))");

        if (rResponseObject.inherits("try-error")) {
            System.out.println("ERROR: R Serve Eval Exception : " + rResponseObject.asString());
            //throw new REXPMismatchException(rResponseObject, rResponseObject.asString());
            return new ForecastDTO(elementName, rResponseObject.asString()); // We only catch R internal errors. Java errors are thrown up
        } else {
            System.out.println("EVAL OK: " + Rcode);// + rResponseObject.asString());
            RList rlist =  rResponseObject.asList();
            ForecastDTO forecast = new ForecastDTO(
                    elementName,
                    rlist.at(0).asDoubles(), //lower80
                    rlist.at(1).asDoubles(), //lower95
                    rlist.at(2).asDoubles(), //mean
                    rlist.at(3).asDoubles(), //upper80
                    rlist.at(4).asDoubles());//upper95

            return forecast;
        }
    }

    static String getModelsDirectory(RConnection rconnection) throws REXPMismatchException, REngineException {
        String getwd = "try(eval(paste(getwd(), directoryToSave, sep = '/')))";
        return getRvariable(rconnection, getwd)[0];
    }

    static String[] getAvailableMethods(RConnection rconnection) throws REXPMismatchException, REngineException {
        return getRvariable(rconnection,"stringMethods");
    }

    static String[] getRvariable(RConnection rconnection, String Rvariable) throws REXPMismatchException, REngineException {
        REXP rResponseObject = null;
        rResponseObject = rconnection.parseAndEval("try(eval(" + Rvariable + "))");

        if (rResponseObject.inherits("try-error")) {
            System.out.println("ERROR: R Serve Eval Exception : " + rResponseObject.asString());
            throw new REXPMismatchException(rResponseObject, rResponseObject.asString());
        } else {
            return rResponseObject.asStrings();
        }
    }
}
