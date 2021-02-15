package Forecast;

public enum error_index {
    RMSE("2"),
    MAE("3");
    String index;

    error_index(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }
}
