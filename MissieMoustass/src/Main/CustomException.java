package Main;

// Classe CustomException pour gérer des cas spécifiques
/**
 * Exception personnalisée pour gérer des erreurs spécifiques dans l'application.
 * Cette exception permet de signaler des conditions d'erreur particulières dans le traitement.
 */
public class CustomException extends Exception {

	private static final long serialVersionUID = 1L;

	// Constructeur sans argument
    /**
     * Constructeur par défaut de l'exception personnalisée.
     * Initialise l'exception sans message d'erreur spécifique.
     */
	public CustomException() {
		super();
	}

	// Constructeur avec un message d'erreur
    /**
     * Constructeur de l'exception personnalisée.
     * @param message Le message d'erreur à afficher
     */
	public CustomException(String message) {
		super(message);
	}

	// Constructeur avec une cause
    /**
     * Constructeur de l'exception personnalisée avec une cause.
     * Utilisé pour transmettre une exception précédente à la nouvelle exception sans message d'erreur spécifique.
     * @param cause La cause de l'exception (une autre exception qui a conduit à cette erreur)
     */
	public CustomException(Throwable cause) {
		super(cause);
	}

	// Constructeur avec un message et une cause
    /**
     * Constructeur de l'exception personnalisée avec un message d'erreur et une cause.
     * Utilisé pour transmettre une exception précédente à la nouvelle exception.
     * @param message Le message d'erreur à afficher
     * @param cause La cause de l'exception (une autre exception qui a conduit à cette erreur)
     */
	public CustomException(String message, Throwable cause) {
		super(message, cause);
	}
}