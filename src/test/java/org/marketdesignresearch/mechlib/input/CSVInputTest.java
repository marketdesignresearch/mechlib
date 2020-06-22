package org.marketdesignresearch.mechlib.input;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.math.RoundingMode;
import java.nio.file.Paths;

import org.junit.Test;
import org.marketdesignresearch.mechlib.core.Payment;
import org.marketdesignresearch.mechlib.core.bid.bundle.BundleExactValueBids;
import org.marketdesignresearch.mechlib.input.csv.CsvBidsReader;
import org.marketdesignresearch.mechlib.outcomerules.ccg.MechanismFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.Norm;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.NormFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.paymentrules.VariableNormCCGFactory;
import org.marketdesignresearch.mechlib.outcomerules.ccg.referencepoint.VCGReferencePointFactory;
import org.marketdesignresearch.mechlib.outcomerules.vcg.XORVCGRule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CSVInputTest {

    @Test
    public void testSimpleInput() throws FileNotFoundException {
        BundleExactValueBids bids = CsvBidsReader.csvToXORBids(Paths.get("src/test/resources/CsvBidsTestFile.csv"));
        MechanismFactory quadraticCCG = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
            NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        Payment ccgPayment = quadraticCCG.getOutcomeRule(bids).getPayment();
        log.info(ccgPayment.toString());
        assertThat(ccgPayment.getTotalPayments()).isEqualByComparingTo("20");
    }

    @Test
    public void testRealInput() throws FileNotFoundException {
        BundleExactValueBids bids = CsvBidsReader.csvToXORBids(Paths.get("src/test/resources/CsvGsvmTestFile.csv"));
        MechanismFactory quadraticCCG = new VariableNormCCGFactory(new VCGReferencePointFactory(), NormFactory.withEqualWeights(Norm.MANHATTAN),
                NormFactory.withEqualWeights(Norm.EUCLIDEAN));
        Payment ccgPayment = quadraticCCG.getOutcomeRule(bids).getPayment();
        assertThat(ccgPayment.getTotalPayments().setScale(2, RoundingMode.HALF_UP)).isEqualByComparingTo("509.78");
        log.info(ccgPayment.toString());
        Payment vcgPayment = new XORVCGRule(bids).getPayment();
        assertThat(vcgPayment.getTotalPayments()).isLessThan(ccgPayment.getTotalPayments());
    }

}
