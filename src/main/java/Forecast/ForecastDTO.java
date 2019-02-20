package Forecast;

import java.util.Arrays;

public class ForecastDTO {
    private double[] lower80;
    private double[] lower95;
    private double[] mean;
    private double[] upper80;
    private double[] upper95;
    private String id;
    private String error;

    public ForecastDTO() {
        this.lower80 = new double[]{0};
        this.lower95 = new double[]{0};
        this.mean = new double[]{0};
        this.upper80 = new double[]{0};
        this.upper95 = new double[]{0};
        this.id = "";
        this.error = "";
    }

    public ForecastDTO(String id, String error) {
        this.id = id;
        this.error = error;
    }

    public ForecastDTO(String id, double[] lower80, double[] lower95, double[] mean, double[] upper80, double[] upper95) {
        this.lower80 = lower80;
        this.lower95 = lower95;
        this.mean = mean;
        this.upper80 = upper80;
        this.upper95 = upper95;
        this.id = id;
    }

    public double[] getLower80() {
        return lower80;
    }

    public void setLower80(double[] lower80) {
        this.lower80 = lower80;
    }

    public double[] getLower95() {
        return lower95;
    }

    public void setLower95(double[] lower95) {
        this.lower95 = lower95;
    }

    public double[] getMean() {
        return mean;
    }

    public void setMean(double[] mean) {
        this.mean = mean;
    }

    public double[] getUpper80() {
        return upper80;
    }

    public void setUpper80(double[] upper80) {
        this.upper80 = upper80;
    }

    public double[] getUpper95() {
        return upper95;
    }

    public void setUpper95(double[] upper95) {
        this.upper95 = upper95;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ForecastDTO setIDandReturn(String id) {
        this.setId(id);
        return this;
    }

    @Override
    public String toString() {
        if (error == null) {
            return "\nID:                            " + this.id +
                    "\nLower 80 conf. Pred. Interval: " + Arrays.toString(lower80) +
                    "\nLower 95 conf. Pred. Interval: " + Arrays.toString(lower95) +
                    "\nMean:                          " + Arrays.toString(mean) +
                    "\nUpper 80 conf. Pred. Interval: " + Arrays.toString(upper80) +
                    "\nUpper 95 conf. Pred. Interval: " + Arrays.toString(upper95) +
                    "\n";
        } else {
            return "\nID:                            " + this.id +
                    "\nR ERROR: " + this.error;
        }
    }
}
