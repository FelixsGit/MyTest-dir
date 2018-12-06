package currency_converter.domain;

import org.springframework.stereotype.Service;

public interface RatesDTO {

    double getTodollar();
    double getTosek();
    double getTopound();
    double getToeuro();
    String getCurrency();
}
