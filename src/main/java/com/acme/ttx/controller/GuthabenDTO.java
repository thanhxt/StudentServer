package com.acme.ttx.controller;

import lombok.Builder;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Studenten.
 *
 * @param betrag Betrag
 * @param waehrung Währung
 */
@Builder
record GuthabenDTO(
    BigDecimal betrag,
    Currency waehrung
) {
}
