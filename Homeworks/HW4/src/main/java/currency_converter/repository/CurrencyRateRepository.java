package currency_converter.repository;

import currency_converter.domain.Rates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface CurrencyRateRepository extends JpaRepository<Rates, String> {

    Rates findByCurrency(String currency);
}
