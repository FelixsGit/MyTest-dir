package currency_converter.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rates")
public class Rates implements RatesDTO {

    @Column(name = "tosek")
    private double tosek;

    @Column(name = "todollar")
    private double todollar;

    @Column(name = "topound")
    private double topound;

    @Column(name = "toeuro")
    private double toeuro;

    @Id
    @Column(name = "currency")
    private String currency;


    /**
     * Required by jpa, should not be used
     */
    protected Rates(){

    }
    @Override
    public double getTodollar() {
        return todollar;
    }

    @Override
    public double getTosek() {
        return tosek;
    }

    @Override
    public double getTopound() {
        return topound;
    }

    @Override
    public double getToeuro() {
        return toeuro;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    public void setTosek(double tosek) {
        this.tosek = tosek;
    }

    public void setTodollar(double todollar) {
        this.todollar = todollar;
    }

    public void setTopound(double topound) {
        this.topound = topound;
    }

    public void setToeuro(double toeuro) {
        this.toeuro = toeuro;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
