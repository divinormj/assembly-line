package br.com.neogrid.challenge.domain.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.neogrid.challenge.domain.exception.ProcessException;
import br.com.neogrid.challenge.domain.model.AssemblyLine;
import br.com.neogrid.challenge.domain.model.Response;
import br.com.neogrid.challenge.domain.service.FileStorageService.StreamStorage;

/**
 * Class controls the processing of the input file and generation of the production lines.
 * @author Divino Martins
 *
 */
@Service
public class ProcessFileService {
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private OutputStreamGenerateService outputService;
	
	@Autowired
	private ProcessStepsService processStepsService;

	/**
	 * Starts processing the standard file (input.txt)
	 */
	public Response start() {
		StreamStorage input = fileStorageService.read();
			
		return this.processInput(input);
	}
	
	/**
	 * Process the input file by providing a combination of process steps on the assembly lines.
	 * @param input
	 * @throws Exception
	 */
	public Response processInput(StreamStorage input) {
		if(input == null
				|| input.getStream() == null) {
			throw new ProcessException("The input stream was not informed.");
		}
		
		Response response = Response.builder().input(input).build();
		
		try {
			List<String> titles = this.getTitlesFromInput(input.getStream());
			response.setTitles(titles);
			
			List<AssemblyLine> assemblyLines = processStepsService.generateAssemblyLine(titles);
			response.setAssemblyLines(assemblyLines);
			
			StreamStorage output = outputService.createOutputFile(assemblyLines);
			response.setOutput(output);
			
			return response;
		} catch (Exception ex) {
			StreamStorage outputError = outputService.createErrorOutputFile(ex.getMessage());
			
			response.setOutput(outputError);
			
			throw new ProcessException("It was not possible to generate the assembly lines.", ex, response);
		}
	}
	
	/**
	 * Get a collection with the step titles, corresponding to the line of the input file.
	 * @param file
	 * @return the list of titles or null if there is an error or the file is empty.
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private List<String> getTitlesFromInput(InputStream stream) {
		List<String> titles = null;
		
		try {
			titles = IOUtils.readLines(stream);	
			
			titles = titles.stream().filter(title -> !title.isBlank()).collect(Collectors.toList());
		} catch (IOException ex) {
			throw new ProcessException("The input file could not be read.");
		}
		
		if(titles.isEmpty()) {
			throw new ProcessException("The input file is empty.");
		}
		
		return titles;
	}	
	
}
