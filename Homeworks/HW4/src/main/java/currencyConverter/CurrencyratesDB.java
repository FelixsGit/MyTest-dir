package currencyConverter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CurrencyratesDB {

     @Id
     private String currency;
     private double toDollar;
     private double fromDollar;

    public String getCurrency() {
        return currency;
    }

    public double getToDollar() {
        return toDollar;
    }

    public double getFromDollar() {
        return fromDollar;
    }
}
