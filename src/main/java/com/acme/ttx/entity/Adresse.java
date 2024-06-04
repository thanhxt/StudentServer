package com.acme.ttx.entity;

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

import java.util.UUID;

/**
 * Adressdaten für die Anwendungslogik und zum Abspeichern in der DB.
 */

@Entity
@Table(name="adresse")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Adresse {
    /**
     * Konstante für den regulären Ausdruck einer Postleitzahl als 5-stellige Zahl mit führender Null.
     */
    public static final String PLZ_PATTERN = "^\\d{5}$";

    @Id
    @GeneratedValue
    private UUID id;

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
