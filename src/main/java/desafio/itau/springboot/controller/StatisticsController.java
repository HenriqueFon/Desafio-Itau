package desafio.itau.springboot.controller;

import java.util.DoubleSummaryStatistics;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.itau.springboot.dto.StatisticsResponse;
import desafio.itau.springboot.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/estatistica")
@Tag(name = "Estatísticas", description = "Endpoint para cálculo das estatísticas das transações")
public class StatisticsController {

    private final TransactionService transactionService;

    public StatisticsController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(
            summary = "Calcular estatísticas",
            description = "Retorna as estatísticas das transações realizadas nos últimos 60 segundos."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estatísticas calculadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StatisticsResponse.class)
                    )
            )
    })
    public ResponseEntity<StatisticsResponse> getStatistics() {
        DoubleSummaryStatistics stats = transactionService.getStatistics();

        return ResponseEntity.ok(new StatisticsResponse(stats));
    }
}