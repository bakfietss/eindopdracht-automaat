package nl.automaat.api.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// End-to-end integratietest: boot de hele app op H2, laadt data.sql en loopt de
// runtime-flows langs (seed-data, btw-berekening, M:N-koppeling, upload/download, bon-PDF).
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.sql.init.mode=always",
        "spring.jpa.defer-datasource-initialization=true"
})
class CompleteFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @WithMockUser(roles = "CASHIER")
    void seedData_customersAreLoaded() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Pieter Bakker"));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void seededInvoice_hasServerComputedAmounts() throws Exception {
        // reparatie 1 = onderdeel 1 (59.95) + 2 (54.95) = 114.90 -> btw 24.13 -> 139.03
        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vatAmount").value(24.13))
                .andExpect(jsonPath("$.totalAmount").value(139.03));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void seededRepair_hasLinkedParts() throws Exception {
        mockMvc.perform(get("/repairs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.parts.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void invoicePdf_isDownloadable() throws Exception {
        mockMvc.perform(get("/invoices/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void createInvoiceForOpenRepair_computesBtwCorrectly() throws Exception {
        // reparatie 2 = onderdeel 3 (89.95) + 4 (74.50) = 164.45 -> btw 34.53 -> 198.98
        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repairId\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vatAmount").value(34.53))
                .andExpect(jsonPath("$.totalAmount").value(198.98));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    void uploadAndDownloadCarDocument() throws Exception {
        MockMultipartFile pdf = new MockMultipartFile(
                "file", "papieren.pdf", "application/pdf",
                "%PDF-1.4 test".getBytes());

        mockMvc.perform(multipart("/cars/1/document").file(pdf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").exists());

        mockMvc.perform(get("/cars/1/document"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }
}
