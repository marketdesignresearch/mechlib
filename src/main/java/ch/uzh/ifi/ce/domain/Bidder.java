package ch.uzh.ifi.ce.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.NotImplementedException;

import java.io.Serializable;
import java.math.BigDecimal;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Bidder implements Serializable {
    private static final long serialVersionUID = -4896848195956099257L;
    
    @Getter
    private final String id;

    public BigDecimal getValue(Bundle bundle) {
        throw new NotImplementedException("The Bidder class in its basic form does not have a value function available. You must create bidders yourself in a specific domain and include such value functions.");
    }
}
