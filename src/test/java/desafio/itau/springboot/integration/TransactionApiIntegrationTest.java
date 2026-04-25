package desafio.itau.springboot.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "app.statistics.window-seconds=60")
@AutoConfigureMockMvc
class TransactionApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(delete("/transacao"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTransactionAndReturnCreated() throws Exception {
        String body = """
                {
                    "valor": 123.45,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().minusSeconds(10));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnUnprocessableEntityWhenTransactionValueIsNegative() throws Exception {
        String body = """
                {
                    "valor": -10.0,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().minusSeconds(10));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("valor"));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenTransactionDateIsInTheFuture() throws Exception {
        String body = """
                {
                    "valor": 100.0,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().plusMinutes(5));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.errors[0].field").value("dataHora"));
    }

    @Test
    void shouldReturnUnprocessableEntityWhenRequiredFieldsAreMissing() throws Exception {
        String body = """
                {
                }
                """;

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsInvalid() throws Exception {
        String invalidJson = """
                {
                    "valor": 100.0,
                    "dataHora":
                }
                """;

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    void shouldClearTransactions() throws Exception {
        String body = """
                {
                    "valor": 100.0,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().minusSeconds(10));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/transacao"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.sum").value(0.0))
                .andExpect(jsonPath("$.avg").value(0.0))
                .andExpect(jsonPath("$.min").value(0.0))
                .andExpect(jsonPath("$.max").value(0.0));
    }

    @Test
    void shouldReturnStatisticsOnlyFromLast60Seconds() throws Exception {
        String recentTransaction = """
                {
                    "valor": 100.0,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().minusSeconds(10));

        String oldTransaction = """
                {
                    "valor": 999.0,
                    "dataHora": "%s"
                }
                """.formatted(OffsetDateTime.now().minusSeconds(120));

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recentTransaction))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/transacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(oldTransaction))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/estatistica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.sum").value(100.0))
                .andExpect(jsonPath("$.avg").value(100.0))
                .andExpect(jsonPath("$.min").value(100.0))
                .andExpect(jsonPath("$.max").value(100.0));
    }
}