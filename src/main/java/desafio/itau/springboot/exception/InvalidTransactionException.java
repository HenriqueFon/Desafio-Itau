package desafio.itau.springboot.exception;

public class InvalidTransactionException extends RuntimeException {

    private final String field;

    public InvalidTransactionException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
