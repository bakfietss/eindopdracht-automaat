package nl.automaat.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtRoleConverterTest {

    private final JwtRoleConverter converter = new JwtRoleConverter();

    @Test
    void convert_mapsRealmRolesToAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("roles", List.of("ROLE_MECHANIC", "ROLE_CASHIER")))
                .build();

        Collection<GrantedAuthority> result = converter.convert(jwt);

        assertThat(result)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_MECHANIC", "ROLE_CASHIER");
    }

    @Test
    void convert_noRealmAccess_returnsEmpty() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "monteur")
                .build();

        assertThat(converter.convert(jwt)).isEmpty();
    }

    @Test
    void convert_realmAccessWithoutRoles_returnsEmpty() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("realm_access", Map.of("something", "else"))
                .build();

        assertThat(converter.convert(jwt)).isEmpty();
    }
}
