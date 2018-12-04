package currencyConverter;

import ch.qos.logback.classic.LoggerContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args){

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CurrencyExchanger");
        EntityManager em = emf.createEntityManager();

        CurrencyratesDB SEK = em.find(CurrencyratesDB.class,"SEK");

        CurrencyratesDB EURO = em.find(CurrencyratesDB.class,"EURO");

        CurrencyratesDB POUND = em.find(CurrencyratesDB.class,"POUND");

        //I want to convert 15 SEK to POUND

        double inPound = 15*SEK.getToDollar()*POUND.getFromDollar();
        System.out.println("15 SEK = "+inPound+" POUNDS");




    }
}
