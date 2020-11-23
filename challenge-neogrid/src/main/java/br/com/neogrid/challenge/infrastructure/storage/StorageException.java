package br.com.neogrid.challenge.infrastructure.storage;

/**
 * Class to handle exception when writing or reading files.
 * @author Divino Martins
 *
 */
public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StorageException(String message) {
		super(message);
	}
	
	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
