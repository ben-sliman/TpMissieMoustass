package Main;

// Classe CustomException pour gérer des cas spécifiques
public class CustomException extends Exception {

	private static final long serialVersionUID = 1L;

	// Constructeur sans argument
	public CustomException() {
		super();
	}

	// Constructeur avec un message d'erreur
	public CustomException(String message) {
		super(message);
	}

	// Constructeur avec une cause
	public CustomException(Throwable cause) {
		super(cause);
	}

	// Constructeur avec un message et une cause
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}
}