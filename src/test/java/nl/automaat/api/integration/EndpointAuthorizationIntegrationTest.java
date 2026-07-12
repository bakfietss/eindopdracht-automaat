package nl.automaat.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// verifieert de autorisatiematrix: iedereen mag lezen, alleen de juiste rol mag schrijven
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EndpointAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // gemockt zodat de context niet echt keycloak hoeft te bereiken
    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    void getCustomers_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void getCustomers_withAnyAuthenticatedRole_returnsOk() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void createCustomer_asMechanic_isForbidden() throws Exception {
        // klanten beheren mag alleen CASHIER -> 403 voor MECHANIC
        mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BACKOFFICE")
    void createPart_asBackoffice_passesSecurity() throws Exception {
        // BACKOFFICE mag onderdelen beheren -> security OK, lege body -> 400 validatie
        mockMvc.perform(post("/parts")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MECHANIC")
    void createInspection_asMechanic_passesSecurity() throws Exception {
        // MECHANIC mag keuringen beheren -> security OK, lege body -> 400 validatie
        mockMvc.perform(post("/inspections")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
