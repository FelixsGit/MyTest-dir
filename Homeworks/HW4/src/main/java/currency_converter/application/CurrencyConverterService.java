package currency_converter.application;

import currency_converter.domain.ConvCounter;
import currency_converter.domain.ConvCounterDTO;
import currency_converter.domain.Rates;
import currency_converter.domain.RatesDTO;
import currency_converter.presentation.conv.Admin;
import currency_converter.repository.CurrencyRateRepository;
import currency_converter.repository.NumberOfConversionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
public class CurrencyConverterService {

    @Autowired
    private CurrencyRateRepository currencyRateRepository;
    @Autowired
    private NumberOfConversionRepository numberOfConversionRepository;

    public RatesDTO findRates(String currency) {
        ConvCounter convCounter = numberOfConversionRepository.findByCounter("counter");
        convCounter.incrementConversions();
        return currencyRateRepository.findByCurrency(currency);
    }

    public ConvCounterDTO findNumOfConversions(){
        return numberOfConversionRepository.findByCounter("counter");

    }

    public void updateCurrencyRates(Admin admin){
        Rates rates = currencyRateRepository.findByCurrency(admin.getFromCurrency());

        //updating the selected row
        rates.setTodollar(admin.getTodollar());
        rates.setToeuro(admin.getToeuro());
        rates.setTopound(admin.getTopound());
        rates.setTosek(admin.getTosek());

        //because of dependency in the db we have to update the other affected rows as well.
        //TODO change db structure by removing redundancy
        if(admin.getFromCurrency().equals("SEK")){
            rates= currencyRateRepository.findByCurrency("SEK");
            rates.setTosek(1/admin.getTosek());
            rates = currencyRateRepository.findByCurrency("USD");
            rates.setTosek(1/admin.getTodollar());
            rates = currencyRateRepository.findByCurrency("GBP");
            rates.setTosek(1/admin.getTopound());
            rates = currencyRateRepository.findByCurrency("EUR");
            rates.setTosek(1/admin.getToeuro());
        }else if(admin.getFromCurrency().equals("USD")){
            rates= currencyRateRepository.findByCurrency("SEK");
            rates.setTodollar(1/admin.getTosek());
            rates = currencyRateRepository.findByCurrency("USD");
            rates.setTodollar(1/admin.getTodollar());
            rates = currencyRateRepository.findByCurrency("GBP");
            rates.setTodollar(1/admin.getTopound());
            rates = currencyRateRepository.findByCurrency("EUR");
            rates.setTodollar(1/admin.getToeuro());
        }else if(admin.getFromCurrency().equals("GBP")){
            rates= currencyRateRepository.findByCurrency("SEK");
            rates.setTopound(1/admin.getTosek());
            rates = currencyRateRepository.findByCurrency("USD");
            rates.setTopound(1/admin.getTodollar());
            rates = currencyRateRepository.findByCurrency("GBP");
            rates.setTopound(1/admin.getTopound());
            rates = currencyRateRepository.findByCurrency("EUR");
            rates.setTopound(1/admin.getToeuro());
        }else if(admin.getFromCurrency().equals("EUR")){
            rates= currencyRateRepository.findByCurrency("SEK");
            rates.setToeuro(1/admin.getTosek());
            rates = currencyRateRepository.findByCurrency("USD");
            rates.setToeuro(1/admin.getTodollar());
            rates = currencyRateRepository.findByCurrency("GBP");
            rates.setToeuro(1/admin.getTopound());
            rates = currencyRateRepository.findByCurrency("EUR");
            rates.setToeuro(1/admin.getToeuro());
        }
    }
}
