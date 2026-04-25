package desafio.itau.springboot.controller;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import desafio.itau.springboot.dto.TransactionRequest;
import desafio.itau.springboot.exception.InvalidTransactionException;
import desafio.itau.springboot.model.Transaction;
import desafio.itau.springboot.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transacao")
@Tag(name = "Transações", description = "Endpoints para registrar e limpar transações")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @Operation(
            summary = "Receber transação",
            description = "Registra uma transação válida em memória. A transação não pode ter valor negativo e não pode estar no futuro."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Transação aceita e registrada com sucesso",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida ou JSON malformado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Transação inválida. Exemplo: valor negativo ou data/hora no futuro",
                    content = @Content
            )
    })
    public ResponseEntity<Void> createTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da transação a ser registrada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TransactionRequest.class))
            )
            @Valid @RequestBody TransactionRequest request
    ) {
        if (request.getDataHora().isAfter(OffsetDateTime.now())) {
            throw new InvalidTransactionException(
                    "dataHora",
                    "A data e hora da transação não pode estar no futuro."
            );
        }

        transactionService.addTransaction(new Transaction(request.getValor(), request.getDataHora()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    @Operation(
            summary = "Limpar transações",
            description = "Remove todas as transações armazenadas em memória."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Todas as transações foram removidas com sucesso",
                    content = @Content
            )
    })
    public ResponseEntity<Void> clearTransactions() {
        transactionService.clearTransactions();

        return ResponseEntity.ok().build();
    }
}