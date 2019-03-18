package ch.uzh.ifi.ce.domain.cats;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CATSAuction implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3253471183746973026L;
    private int numberOfGoods;
    private int numberOfBids;
    private int numberOfDummyGoods;
    private final List<CATSBid> catsBids = new ArrayList<>();
    private String distribution = null;
    private Path fileName;

    public List<CATSBid> getCatsBids() {
        return catsBids;
    }

    public void addCatsBid(CATSBid catsBid) {
        this.catsBids.add(catsBid);
    }

    public int getNumberOfGoods() {
        return numberOfGoods;
    }

    public void setNumberOfGoods(int numberOfGoods) {
        this.numberOfGoods = numberOfGoods;
    }

    public int getNumberOfBids() {
        return numberOfBids;
    }

    public void setNumberOfBids(int numberOfBids) {
        this.numberOfBids = numberOfBids;
    }

    public int getNumberOfDummyGoods() {
        return numberOfDummyGoods;
    }

    public void setNumberOfDummyGoods(int numberOfDummyGoods) {
        this.numberOfDummyGoods = numberOfDummyGoods;
    }

    public void setDistribution(CharSequence subSequence) {
        distribution = subSequence.toString();
    }

    public String getDistribution() {
        return distribution;
    }

    @Override
    public String toString() {
        return "CATSAuction[distribution=" + distribution + " ,numberOfBids=" + numberOfBids + " ,numberOfGoods=" + numberOfGoods + " ,numberOfDummyGoods=" + numberOfDummyGoods
                + "]";
    }

    public Path getFileName() {
        return fileName;
    }

    public void setFileName(Path fileName) {
        this.fileName = fileName;
    }

}
