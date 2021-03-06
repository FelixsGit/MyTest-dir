package currency_converter.presentation.conv;

import currency_converter.application.CurrencyConverterService;
import currency_converter.domain.ConvCounterDTO;
import currency_converter.domain.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.validation.Valid;
import java.text.DecimalFormat;

@Controller
@Scope("session")
public class ConversionController {

    @Autowired
    private CurrencyConverterService service;
    private RatesDTO currentConversionRates;
    private ConvCounterDTO currentAmountOfConversions;


    @GetMapping("/conversion")
    public String conversionForm(Model model) {
        model.addAttribute("conversion", new Conversion());
        return "conversion";
    }

    @GetMapping("/admin")
    public String adminForm(Model model) {
        model.addAttribute("numberOfConversions", new NumberOfConversions());
        model.addAttribute("admin", new Admin());
        currentAmountOfConversions = service.findNumOfConversions();
        model.addAttribute("numberOfConversions", currentAmountOfConversions);
        return "admin";
    }


    @PostMapping("/admin")
    public String adminSubmit(@Valid @ModelAttribute Admin updatedRates, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            currentAmountOfConversions = service.findNumOfConversions();
            model.addAttribute("numberOfConversions", currentAmountOfConversions);
            return "admin";
        }
        currentAmountOfConversions = service.findNumOfConversions();
        model.addAttribute("numberOfConversions", currentAmountOfConversions);
        service.updateCurrencyRates(updatedRates);
        return "admin";
    }


    @PostMapping("/conversion")
    public String conversionSubmit(@Valid @ModelAttribute Conversion conversion, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "conversion";
        }
        currentConversionRates = service.findRates(conversion.getFromCurrency());
        double convertedAmount;
        if(conversion.getToCurrency().equals("SEK")){
            convertedAmount = currentConversionRates.getTosek() * conversion.getAmount();
        }
        else if(conversion.getToCurrency().equals("USD")){
            convertedAmount = currentConversionRates.getTodollar() * conversion.getAmount();
        }
        else if(conversion.getToCurrency().equals("GBP")){
            convertedAmount = currentConversionRates.getTopound() * conversion.getAmount();
        }
        else{
            convertedAmount = currentConversionRates.getToeuro() * conversion.getAmount();
        }
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        String amountToReturn = numberFormat.format(convertedAmount);
        conversion.setAmountAfter(amountToReturn);
        return "result";
    }


}
