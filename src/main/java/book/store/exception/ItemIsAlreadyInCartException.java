package book.store.exception;

public class ItemIsAlreadyInCartException extends RuntimeException {
    public ItemIsAlreadyInCartException(String message) {
        super(message);
    }
}
