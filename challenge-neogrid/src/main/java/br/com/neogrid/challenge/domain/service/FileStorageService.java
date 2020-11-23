package br.com.neogrid.challenge.domain.service;

import java.io.InputStream;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * Interface for file recording implementations.
 * @author Divino Martins
 *
 */
public interface FileStorageService {
	
	StreamStorage read(String name);
	
	void save(StreamStorage file);
	
	default StreamStorage read() {
		return this.read(getDefaultInputFileName());
	}
	
	default String getDefaultInputFileName() {
		return "input.txt";
	}
	
	default String generateOutputFileName() {
		return "output_" + UUID.randomUUID() +  ".txt";
	}
	
	default String generateOutputErrorFileName() {
		return "output_error.txt";
	}
		
	@Builder
	@Data
	class StreamStorage {
		
		private String path;
		private String name;
		private InputStream stream;
	}
}
