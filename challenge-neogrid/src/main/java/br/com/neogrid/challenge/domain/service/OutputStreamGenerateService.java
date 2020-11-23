package br.com.neogrid.challenge.domain.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.neogrid.challenge.domain.model.AssemblyLine;
import br.com.neogrid.challenge.domain.service.FileStorageService.StreamStorage;
import br.com.neogrid.challenge.infrastructure.storage.StorageException;

/**
 * Class responsible for generating the output file with the assembly lines.
 * @author Divino Martins
 *
 */
@Service
public class OutputStreamGenerateService {
	
	@Autowired
	private FileStorageService fileStorageService;

	public StreamStorage createOutputFile(List<AssemblyLine> assemblyLines) {
		if(assemblyLines == null
				|| assemblyLines.isEmpty()) {
			throw new StorageException("The assembly line was not informed.");
		}
		
		List<String> outputList = new ArrayList<>();
		
		for (AssemblyLine assemblyLine : assemblyLines) {
			List<String> steps = assemblyLine.getSteps().stream()
					.map(step -> String.format("%s %s", step.getTime().toString(), step.getTitle()))
					.collect(Collectors.toList());
			
			outputList.add(assemblyLine.getDescription());
			outputList.addAll(steps);
			outputList.add("");
		}

		InputStream inputStream = this.convertListToInputStream(outputList);
		
		StreamStorage output = StreamStorage.builder()
				.name(fileStorageService.generateOutputFileName())
				.stream(inputStream)
				.build();
		
		fileStorageService.save(output);
		
		return output;
	}
	
	public StreamStorage createErrorOutputFile(String message) {
		List<String> outputList = new ArrayList<>();
		
		outputList.add("Não foi possível gerar as linhas de montagem");
		outputList.add("Erro:");
		outputList.add(message);
		
		InputStream inputStream = this.convertListToInputStream(outputList);
		
		StreamStorage output = StreamStorage.builder()
				.name(fileStorageService.generateOutputErrorFileName())
				.stream(inputStream)
				.build();
		
		fileStorageService.save(output);
		
		return output;
	}
	
	private InputStream convertListToInputStream(List<String> list) {
		byte[] bytes = list.stream().collect(Collectors.joining("\n", "", "\n")).getBytes();
		
		return new ByteArrayInputStream(bytes);
	}
	
}
