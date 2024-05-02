package com.acme.ttx.entity;

import java.math.BigDecimal;
import java.util.Currency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Geldbetrag und Währungseinheit für ein Guthaben.
 */
@Builder
@Getter
@Setter
@ToString
public class Guthaben {
    /**
     * Der Betrag beim Guthaben.
     */
    private BigDecimal betrag;

    /**
     * Die Währung beim Guthaben.
     */
    private Currency waehrung;

}
