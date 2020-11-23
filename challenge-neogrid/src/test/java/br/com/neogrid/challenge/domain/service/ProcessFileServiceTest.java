package br.com.neogrid.challenge.domain.service;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.neogrid.challenge.domain.exception.ProcessException;
import br.com.neogrid.challenge.domain.model.Response;
import br.com.neogrid.challenge.domain.service.FileStorageService.StreamStorage;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ProcessFileServiceTest {
	
	@Autowired
	private ProcessFileService service;
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenFileStorageIsNull() {
		service.processInput(null);
	}

	@Test(expected = ProcessException.class)
	public void shouldFailWhenInputStreamIsNull() {
		FileStorageService.StreamStorage fileStorage = StreamStorage.builder().build();
		
		service.processInput(fileStorage);
	}
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenInputStreamIsEmpty() {
		List<String> input = new ArrayList<>();
		byte[] bytes = input.stream().collect(Collectors.joining("\n", "", "\n")).getBytes();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		FileStorageService.StreamStorage fileStorage = StreamStorage.builder()
				.stream(inputStream)
				.build();
		
		service.processInput(fileStorage);
	}
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenInputStreamNotValid() {
		List<String> input = new ArrayList<>();
		
		input.add("Step one of the production process");
		
		byte[] bytes = input.stream().collect(Collectors.joining("\n", "", "\n")).getBytes();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		FileStorageService.StreamStorage fileStorage = StreamStorage.builder()
				.stream(inputStream)
				.build();
		
		service.processInput(fileStorage);
	}
	
	@Test
	public void shouldGenerateOutputStreamWhenProcessingValidInputStream() {
		List<String> input = new ArrayList<>();
		
		input.add("Step maintenance of the production process");
		input.add("Step of the production process 45min");
		
		byte[] bytes = input.stream().collect(Collectors.joining("\n", "", "\n")).getBytes();
		InputStream inputStream = new ByteArrayInputStream(bytes);
		FileStorageService.StreamStorage fileStorage = StreamStorage.builder()
				.stream(inputStream)
				.build();
		
		Response response = service.processInput(fileStorage);
		
		assertNotNull(response.getOutput());
		assertNotNull(response.getOutput().getStream());
	}

}
