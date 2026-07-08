# Keycloak setup

De AutoMaat-API is een OAuth2.0 resource server. Keycloak geeft de tokens uit, de API valideert ze.

## Importeren

1. Start Keycloak in dev-mode op poort 9090:
   ```
   kc.bat start-dev --http-port=9090
   ```
2. Realm importeren via de admin console (Realm settings > Partial import) of bij het starten met `--import-realm` als het bestand in de `data/import`-map staat.
3. Het client secret in de export is een placeholder (`CHANGEME-LOCAL-DEV-SECRET`). Zet in de admin console een eigen secret bij `Clients > automaat-api > Credentials`, of laat Keycloak er een genereren, en vul die in Postman in.

## Testgebruikers

Wachtwoord voor alle drie: `geheim`

| Gebruiker | Rol | Mag beheren |
|-----------|-----|-------------|
| monteur | ROLE_MECHANIC | keuringen, reparaties |
| kassa | ROLE_CASHIER | klanten, auto's, bonnen |
| backoffice | ROLE_BACKOFFICE | onderdelenvoorraad |

Lezen (GET) mag elke ingelogde gebruiker op alle endpoints.
