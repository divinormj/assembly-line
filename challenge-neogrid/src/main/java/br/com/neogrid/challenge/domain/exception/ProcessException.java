package br.com.neogrid.challenge.domain.exception;

import br.com.neogrid.challenge.domain.model.Response;

/**
 * Class to handle exception during generation processing of assembly lines.
 * @author Divino Martins
 *
 */
public class ProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Response response;
	
	public ProcessException(String message) {
		super(message);
	}

	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ProcessException(String message, Throwable cause, Response response) {
		super(message, cause);
		
		this.response = response;
	}
	
	public Response getResponse() {
		return response;
	}

}
