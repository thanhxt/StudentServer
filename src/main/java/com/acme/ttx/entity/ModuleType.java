package com.acme.ttx.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

/**
 * Enum für ein Modul.
 */
public enum ModuleType {
    /**
     * _Softwarearchitektur_ mit dem internen Wert 'SWA' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    SOFTWAREARCHITEKTUR("SWA"),

    /**
     * _Softwareengineering_ mit dem internen Wert 'SWE' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    SOFTWAREENGINEERING("SWE"),

    /**
     * _Programmieren_1_ mit dem internen Wert 'PR1' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    PROGRAMMIEREN_1("PR1"),

    /**
     * _Programmieren_2_ mit dem internen Wert 'PR2' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    PROGRAMMIEREN_2("PR2"),

    /**
     * _Mathematik_1_ mit dem internen Wert 'MA1' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    MATHEMATIK_1("MA1"),

    /**
     * _Mathematik_2_ mit dem internen Wert 'MA2' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern einer DB.
     */
    MATHEMATIK_2("MA2");

    private final String value;

    ModuleType(final String value) {
        this.value = value;
    }

    /**
     * Einen enum-Wert als String mit dem internen Wert ausgeben. Dieser Wert wird durch Jackson in einem
     * JSON-Datensatz verwendet.
     *
     * @return Internen Wert
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Konvertierung eines Strings in einen Enum-Wert.
     *
     * @param value Der String, zu dem ein passender Enum-Wert ermittelt werden soll.
     * @return Passender Enum-Wert oder null.
     */
    public static ModuleType of(final String value) {
        return Stream.of(values())
            .filter(module -> module.value.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
