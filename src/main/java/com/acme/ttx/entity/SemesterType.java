package com.acme.ttx.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

/**
 * Enum für das Semester.
 */
public enum SemesterType {
    /**
     * _Semester_1_ mit dem internen Wert `1` für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_1("1"),

    /**
     * _Semester_2_ mit dem internen Wert '2' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_2("2"),

    /**
     * _Semester_3_ mit dem internen Wert '3' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_3("3"),

    /**
     * _Semester_4_ mit dem internen Wert '4' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_4("4"),

    /**
     * _Semester_5_ mit dem internen Wert '5' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_5("5"),

    /**
     * _Semester_6_ mit dem internen Wert '6' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_6("6"),

    /**
     * _Semester_7_ mit dem internen Wert '7' für z.B. das Mapping in einem JSON-Datensatz oder
     * das Abspeichern in einer DB.
     */
    SEMESTER_7("7");

    private final String value;

    SemesterType(final String value) {
        this.value = value;
    }

    /**
     * Einen enum-Wert als String mit dem internen Wert ausgeben.
     * Dieser Wert wird durch Jackson in einem JSON-Datensatz verwendet.
     * [<a href="https://github.com/FasterXML/jackson-databind/wiki">Wiki-Seiten</a>]
     *
     * @return Interner Wert
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
    @JsonCreator
    public static SemesterType of(final String value) {
        return Stream.of(values())
            .filter(semester -> semester.value.equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
