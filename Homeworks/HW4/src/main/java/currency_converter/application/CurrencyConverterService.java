package currency_converter.application;

import currency_converter.domain.RatesDTO;
import currency_converter.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Transactional(propagation = Propagation.REQUIRES_NEW)
@Service
public class CurrencyConverterService {

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    public RatesDTO findRates(String currency) {
        return currencyRateRepository.findByCurrency(currency);
    }
}
