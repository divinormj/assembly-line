package br.com.neogrid.challenge.core.storage;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties regarding the location of reading and writing the input and output files.
 * @author Divino Martins
 *
 */
@Getter
@Setter
@Component
@ConfigurationProperties("challenge.storage")
public class StorageProperties {
	
	private Local local = new Local();

	@Getter
	@Setter
	public class Local {
		/**
		 * Directory where the input file.
		 */
		private Path directoryInput;
		
		/**
		 * Directory where the output files will be saved.
		 */
		private Path directoryOutput;
		
	}
}
