package currency_converter.presentation.conv;

import currency_converter.util.Util;

import javax.validation.constraints.*;

public class Conversion {

    private String fromCurrency;
    private String toCurrency;

    @NotNull(message = "please enter a value to convert")
    @Positive(message = "value must be greater than zero")
    private double amount;

    private String amountAfter;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getAmountAfter(){
        return amountAfter;
    }

    public void setAmountAfter(String amountAfter){
        this.amountAfter = amountAfter;
    }
    @Override
    public String toString() {
        return Util.toString(this);
    }
}