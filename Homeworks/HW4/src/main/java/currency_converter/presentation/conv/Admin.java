package currency_converter.presentation.conv;

import currency_converter.util.Util;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class Admin {

    private String fromCurrency;
    @NotNull(message = "please enter a value to convert")
    @Positive(message = "value must be greater than zero")
    private double todollar;
    @NotNull(message = "please enter a value to convert")
    @Positive(message = "value must be greater than zero")
    private double topound;
    @NotNull(message = "please enter a value to convert")
    @Positive(message = "value must be greater than zero")
    private double toeuro;
    @NotNull(message = "please enter a value to convert")
    @Positive(message = "value must be greater than zero")
    private double tosek;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public double getTodollar() {
        return todollar;
    }

    public void setTodollar(double todollar) {
        this.todollar = todollar;
    }

    public double getTopound() {
        return topound;
    }

    public void setTopound(double topound) {
        this.topound = topound;
    }

    public double getToeuro() {
        return toeuro;
    }

    public void setToeuro(double toeuro) {
        this.toeuro = toeuro;
    }

    public double getTosek() {
        return tosek;
    }

    public void setTosek(double tosek) {
        this.tosek = tosek;
    }

    @Override
    public String toString() {
        return Util.toString(this);
    }
}
