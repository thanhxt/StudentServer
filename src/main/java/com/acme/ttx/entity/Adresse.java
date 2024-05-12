package com.acme.ttx.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Adressdaten für die Anwendungslogik und zum Abspeichern in der DB.
 */
@Builder
@Getter
@Setter
@ToString
public class Adresse {
    /**
     * Konstante für den regulären Ausdruck einer Postleitzahl als 5-stellige Zahl mit führender Null.
     */
    public static final String PLZ_PATTERN = "^\\d{5}$";

    /**
     * Postleitzahl für eine Adresse.
     */
    private String plz;

    /**
     * Ort für eine Adresse.
     */
    private String ort;

    /**
     * Strasse für die Adresse.
     */
    private String strasse;
}
