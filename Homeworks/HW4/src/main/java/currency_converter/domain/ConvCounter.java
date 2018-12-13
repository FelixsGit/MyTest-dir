package currency_converter.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "visitors")
public class ConvCounter implements ConvCounterDTO {

    @Id
    @Column(name = "counter")
    private String counter;

    @Column(name = "conversions")
    private int conversions;

    public void incrementConversions() {
        conversions++;
    }

    /**
     * Required by jpa, should not be used
     */
    protected ConvCounter(){

    }
    @Override
    public String getCounter() {
        return counter;
    }

    @Override
    public int getConversions() {
        return conversions;
    }

}
