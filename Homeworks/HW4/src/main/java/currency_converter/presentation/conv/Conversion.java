package currency_converter.presentation.conv;

import currency_converter.util.Util;

public class Conversion {

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double amountAfter;

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
    public double getAmountAfter(){
        return amountAfter;
    }

    public void setAmountAfter(double amountAfter){
        this.amountAfter = amountAfter;
    }
    @Override
    public String toString() {
        return Util.toString(this);
    }
}