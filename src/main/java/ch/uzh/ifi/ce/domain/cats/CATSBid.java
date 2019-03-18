package ch.uzh.ifi.ce.domain.cats;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CATSBid implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 7736295648019090804L;
    private int id;
    private BigDecimal amount;
    private List<Integer> goodIds;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(int id) {
        this.id = id;
    }
}
