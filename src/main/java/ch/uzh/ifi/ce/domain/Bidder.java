package ch.uzh.ifi.ce.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Bidder implements Serializable {
    private static final long serialVersionUID = -4896848195956099257L;
    
    @Getter
    private final String id;
}
