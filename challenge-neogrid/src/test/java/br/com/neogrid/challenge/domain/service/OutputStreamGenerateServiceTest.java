package br.com.neogrid.challenge.domain.service;

import static org.junit.Assert.assertNotNull;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.neogrid.challenge.domain.model.AssemblyLine;
import br.com.neogrid.challenge.domain.model.Step;
import br.com.neogrid.challenge.domain.service.FileStorageService.StreamStorage;
import br.com.neogrid.challenge.infrastructure.storage.StorageException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class OutputStreamGenerateServiceTest {
	
	@Autowired
	private OutputStreamGenerateService service;
	
	@Test(expected = StorageException.class)
	public void shouldFailWhenAssemblyLinesIsNull() {
		service.createOutputFile(null);
	}
	
	@Test(expected = StorageException.class)
	public void shouldFailWhenAssemblyLinesIsEmpty() {
		service.createOutputFile(new ArrayList<AssemblyLine>());
	}
	
	@Test
	public void shouldGenerateStreamWhenListAssemblyLineValid() {
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		AssemblyLine assemblyLine = new AssemblyLine();
		Step step = Step.builder()
				.time(LocalTime.now())
				.title("Step of the production process 45min")
				.build();
		
		assemblyLine.setNumber(1);
		assemblyLine.getSteps().add(step);
		
		assemblyLines.add(assemblyLine);
		
		StreamStorage stream = service.createOutputFile(assemblyLines);
		
		assertNotNull(stream);
	}
}
