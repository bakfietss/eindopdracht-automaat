package nl.automaat.api.model;

/**
 * Gebruikersrollen binnen AutoMaat. Worden in Spring Security
 * vertaald naar authorities met prefix ROLE_ (bv. ROLE_MECHANIC).
 */
public enum Role {
    MECHANIC,
    CASHIER,
    BACKOFFICE
}
