package com.acme.ttx.controller;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * ValueObject für das Neuanlegen und Ändern eines neuen Studenten.
 *
 * @param betrag Betrag
 * @param waehrung Währung
 */
record GuthabenDTO(
    BigDecimal betrag,
    Currency waehrung
) {
}
