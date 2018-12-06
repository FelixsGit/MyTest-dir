package currency_converter.presentation.conv;

import currency_converter.application.CurrencyConverterService;
import currency_converter.domain.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Scope("session")
public class ConversionController {

    @Autowired
    private CurrencyConverterService service;
    private RatesDTO currentConversionRates;

    @GetMapping("/conversion")
    public String conversionForm(Model model) {
        model.addAttribute("conversion", new Conversion());
        return "conversion";
    }

    @PostMapping("/conversion")
    public String conversionSubmit(@ModelAttribute Conversion conversion) {
        System.out.println(conversion.toString());
        currentConversionRates = service.findRates(conversion.getFromCurrency());
        double convertedAmount;
        if(conversion.getToCurrency().equals("SEK")){
            convertedAmount = currentConversionRates.getTosek() * conversion.getAmount();
        }
        else if(conversion.getToCurrency().equals("DOLLAR")){
            convertedAmount = currentConversionRates.getTodollar() * conversion.getAmount();
        }
        else if(conversion.getToCurrency().equals("POUND")){
            convertedAmount = currentConversionRates.getTopound() * conversion.getAmount();
        }
        else{
            convertedAmount = currentConversionRates.getToeuro() * conversion.getAmount();
        }
        conversion.setAmount(convertedAmount);
        return "result";
    }
}
