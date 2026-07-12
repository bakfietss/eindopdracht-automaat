# AutoMaat — Backend Web-API

REST web-API die het beheerproces van een autogarage digitaliseert: klant- en
autoregistratie, keuringen, reparaties, onderdelen en het genereren van bonnen
met BTW. NOVI eindopdracht Backend (Spring Boot, Keycloak/OAuth2.0, PostgreSQL).

## Inhoudsopgave

1. [Beschrijving + functionaliteit](#beschrijving--functionaliteit)
2. [Benodigdheden](#benodigdheden)
3. [Projectstructuur + technieken](#projectstructuur--technieken)
4. [Installatie stappenplan](#installatie-stappenplan)
5. [Keycloak instellen](#keycloak-instellen)
6. [Testgebruikers + rollen](#testgebruikers--rollen)
7. [Tests](#tests)

## Beschrijving + functionaliteit

| # | Functionaliteit | Rol |
|---|-----------------|-----|
| 1 | Inloggen via Keycloak, autorisatie per rol | Iedereen |
| 2 | Klanten + auto's beheren, autopapieren (PDF) up/downloaden | Kassamedewerker |
| 3 | Keuringen + reparaties + onderdelen koppelen | Monteur |
| 4 | Bonnen genereren met 21% BTW + betaalstatus + PDF | Kassamedewerker |
| 5 | Onderdelenvoorraad beheren | Backoffice |

## Benodigdheden

- Java 21 (LTS) — bv. Amazon Corretto
- PostgreSQL 16+ draaiend op `localhost:5432` met een database `automaat`
- Keycloak 26+ (voor authenticatie), draaiend op poort `9090`
- Maven (of de meegeleverde wrapper `./mvnw`)
- Postman om de API te testen

## Projectstructuur + technieken

```
src/main/java/nl/automaat/api/
├── controller/   REST endpoints
├── service/      business logic + DTO-mapping
├── repository/   Spring Data JPA
├── dto/          Request/Response/Update DTO's met validatie
├── model/        @Entity klassen + enums
├── exception/    centrale @RestControllerAdvice
├── security/     OAuth2 resource server (SecurityConfig, JwtRoleConverter)
└── util/         PatchUtil voor partial updates
```

Technieken: Spring Boot 3.5, Spring Security + OAuth2 Resource Server (Keycloak),
Spring Data JPA, Hibernate, Bean Validation, Lombok, OpenPDF (bon-PDF),
JUnit 5 + Mockito + H2 (tests).

## Installatie stappenplan

```bash
# 1. Maak een lege database 'automaat' aan in PostgreSQL
#    (gebruiker + wachtwoord staan in application.properties, standaard automaat/automaat)

# 2. Start Keycloak (zie 'Keycloak instellen' hieronder)

# 3. Start de applicatie
cd automaat
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

De API draait op `http://localhost:8080`. Hibernate genereert de tabellen uit
de `@Entity`-klassen (`spring.jpa.hibernate.ddl-auto=create`).

> Pas eventueel `spring.datasource.username/password` in
> `src/main/resources/application.properties` aan op je lokale Postgres.

## Keycloak instellen

De API is een OAuth2.0 resource server: Keycloak geeft de tokens uit, de API
valideert ze alleen. De volledige realm (rollen + testgebruikers) zit als export
in `src/main/resources/keycloak/realm-export.json`.

```bash
# Start Keycloak in dev-mode op poort 9090
kc.bat start-dev --http-port=9090      # Linux/Mac: kc.sh start-dev --http-port=9090
```

1. Log in op de admin console (`http://localhost:9090`, standaard admin/admin).
2. Importeer `realm-export.json` (Realm settings → Partial import, of start met
   `--import-realm` als het bestand in `data/import` staat).
3. Het client secret in de export is een placeholder. Zet een eigen secret bij
   `Clients → automaat-api → Credentials` en gebruik dat in Postman.

Meer detail: `src/main/resources/keycloak/README.md`.

## Testgebruikers + rollen

Uit de Keycloak-realm. Wachtwoord voor alle drie: **`geheim`**.

| Gebruikersnaam | Rol | Mag beheren (CRUD) |
|----------------|-----|--------------------|
| `monteur` | ROLE_MECHANIC | keuringen, reparaties |
| `kassa` | ROLE_CASHIER | klanten, auto's, bonnen |
| `backoffice` | ROLE_BACKOFFICE | onderdelenvoorraad |

Alle geauthenticeerde rollen mogen alle resources **lezen** (GET). Schrijfacties
(POST/PUT/PATCH/DELETE) zijn per rol beperkt. Vraag in Postman een token op bij
Keycloak (OAuth2.0, Authorization Code) en stuur het mee als
`Authorization: Bearer <token>`.

## Tests

```bash
./mvnw test
```

- Unit tests (`CustomerServiceTest`, `InvoiceServiceTest` incl. BTW-berekening,
  `JwtRoleConverterTest`) met Mockito, volgens het Arrange-Act-Assert patroon.
- Integratietest (`EndpointAuthorizationIntegrationTest`) die de autorisatie per
  rol verifieert met `@WithMockUser`.
- Tests draaien op een in-memory H2-database; Keycloak is niet nodig (de
  `JwtDecoder` wordt gemockt).
