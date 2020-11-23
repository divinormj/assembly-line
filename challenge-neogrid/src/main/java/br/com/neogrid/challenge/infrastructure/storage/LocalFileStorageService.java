package br.com.neogrid.challenge.infrastructure.storage;

import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import br.com.neogrid.challenge.core.storage.StorageProperties;
import br.com.neogrid.challenge.domain.service.FileStorageService;

/**
 * Implementation for recording files on the local machine.
 * @author divino
 *
 */
@Service
public class LocalFileStorageService implements FileStorageService {

	@Autowired
	private StorageProperties storageProperties;

	@Override
	public StreamStorage read(String name) {
		try {
			if(name == null) {
				name = this.getDefaultInputFileName();
			}
			
			Path path = storageProperties.getLocal().getDirectoryInput().resolve(Path.of(name));

			StreamStorage input = StreamStorage.builder()
					.name(name)
					.path(path.toString())
					.stream(Files.newInputStream(path))
					.build();
			
			return input;
		} catch (Exception ex) {
			throw new StorageException("File not found", ex);
		}
	}

	@Override
	public void save(StreamStorage file) {
		try {
			Path path = storageProperties.getLocal().getDirectoryOutput().resolve(Path.of(file.getName()));
			
			file.setPath(path.toString());
			
			FileCopyUtils.copy(file.getStream(), Files.newOutputStream(path));
		} catch (Exception e) {
			e.printStackTrace();
			throw new StorageException("Could not save file.", e);
		}		
	}
	
	
}
