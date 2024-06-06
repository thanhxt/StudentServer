package com.acme.ttx.entity;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Geldbetrag und Währungseinheit für ein Guthaben.
 */

@Entity
@Table(name = "guthaben")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Guthaben {
    @Id
    @GeneratedValue
    private UUID id;
        /**
     * Der Betrag beim Guthaben.
     */
    private BigDecimal betrag;

    /**
     * Die Währung beim Guthaben.
     */
    private Currency waehrung;

}
