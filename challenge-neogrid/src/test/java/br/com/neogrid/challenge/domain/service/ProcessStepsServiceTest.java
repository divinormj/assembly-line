package br.com.neogrid.challenge.domain.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.neogrid.challenge.core.storage.PeriodProperties;
import br.com.neogrid.challenge.domain.exception.ProcessException;
import br.com.neogrid.challenge.domain.model.AssemblyLine;
import br.com.neogrid.challenge.domain.model.Step;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ProcessStepsServiceTest {
	
	@Autowired
	private ProcessStepsService service;
	
	@Autowired
	private PeriodProperties periodProperties;
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenFileStorageIsNull() {
		service.generateAssemblyLine(null);
	}
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenFileStorageIsEmpty() {
		service.generateAssemblyLine(new ArrayList<String>());
	}
	
	@Test(expected = ProcessException.class)
	public void shouldFailWhenTitleNotValid() {
		List<String> titles = new ArrayList<>();
		
		titles.add("Step one of the production process");
		
		service.generateAssemblyLine(titles);
	}
	
	@Test
	public void shouldGenerateListAssembleLineWhenProcessingValidTitles() {
		List<String> titles = new ArrayList<>();
		
		titles.add("Step maintenance of the production process");
		titles.add("Step of the production process 45min");
		
		List<AssemblyLine> assemblyLines = service.generateAssemblyLine(titles);
		
		assertFalse(assemblyLines.isEmpty());
	}

	@Test
	public void shouldReturn2WhenNumberStepsNeedsTwoAssemblyLines() {
		List<Step> steps = new ArrayList<>();
		Integer countAssemblyLine = 2;
		
		for(int i = 0; i < 8; i++) {
			steps.add(Step.builder().timeInMinutes(60).build());
		}
		
		assertEquals(countAssemblyLine, service.countAssemblylines(steps, 420L));
	}
	
	@Test
	public void shouldReturnListStepsWithSameAmountListTitle() {
		List<String> titles = new ArrayList<>();
		
		titles.add("Step maintenance of the production process");
		titles.add("Step of the production process 45min");
		
		List<Step> steps = service.generateSteps(titles);
		
		assertEquals(titles.size(), steps.size());
	}
	
	@Test
	public void shouldReturn60WhenStepTitleContainNumber60() {
		String title = "Step of the production process 60min";
		Integer timeInMinutes = 60;
		
		assertEquals(timeInMinutes, service.getStepDurationInMinutes(title));
	}
	
	@Test
	public void shouldReturn5WhenStepTitleContainWordMaintenanceIgnoreCase() {
		String title = "Step maintenance of the production process";
		Integer timeInMinutes = 5;
		
		assertEquals(timeInMinutes, service.getStepDurationInMinutes(title));
		assertEquals(timeInMinutes, service.getStepDurationInMinutes(title.toUpperCase()));
		
		title = "Step mAIntEnAncE of the production process";
		assertEquals(timeInMinutes, service.getStepDurationInMinutes(title));
	}
	
	@Test
	public void shouldAddStepLunchToAllAssemblyLines() {
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		assemblyLines.add(new AssemblyLine());
		assemblyLines.add(new AssemblyLine());
		
		service.addLunchToAssemblyLines(assemblyLines);
		
		for (AssemblyLine assemblyLine : assemblyLines) {
			assertEquals(assemblyLine.getSteps().size(), 1);
			assertTrue(
					service.getLastStep(assemblyLine.getSteps()).getTitle()
					.equals(ProcessStepsService.STEP_TITLE_LUNCH)
				);
		}		
	}
	
	@Test
	public void shouldReturnTheMinTimeForBeginningLaborGymWhenLastStepEndsBeforeThisTime() {
		AssemblyLine assemblyLine = new AssemblyLine();

		assemblyLine.getSteps().add(
				Step.builder()
					.time(LocalTime.of(9, 0))
					.timeInMinutes(60)
					.build()
			);

		assertEquals(
				periodProperties.getAfternoon().getLaborGymMin(), 
				service.getTimeLaborGymnastics(assemblyLine)
			);
	}
	
	@Test
	public void shouldReturn1630WhenLastStepStarts16AndLasts30Minutes() {
		AssemblyLine assemblyLine = new AssemblyLine();
		LocalTime time = LocalTime.of(16, 30);

		assemblyLine.getSteps().add(
				Step.builder()
					.time(LocalTime.of(16, 0))
					.timeInMinutes(30)
					.build()
			);

		assertEquals(
				time, 
				service.getTimeLaborGymnastics(assemblyLine)
			);
	}
	
	@Test
	public void shouldAddStepLaborGymToAllAssemblyLines() {
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		assemblyLines.add(new AssemblyLine());
		assemblyLines.add(new AssemblyLine());
		
		service.addLaborGymnasticsToAssemblyLines(assemblyLines);
		
		for (AssemblyLine assemblyLine : assemblyLines) {			
			assertEquals(assemblyLine.getSteps().size(), 1);
			assertTrue(
					service.getLastStep(assemblyLine.getSteps()).getTitle()
					.equals(ProcessStepsService.STEP_TITLE_LABOR_GYM)
				);
		}		
	}
	
	@Test
	public void shouldAdd3StepsToAssemblyLineAndConsumeThoseStepsFromListProductionStep() {
		List<Step> steps = new ArrayList<>();
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		
		assemblyLines.add(new AssemblyLine());
		
		for(int i = 0; i < 4; i++) {
			steps.add(Step.builder().timeInMinutes(60).build());
		}
				
		service.addAndConsumingStepsForAllAssemblyLines(
				assemblyLines, 
				steps,
				periodProperties.getMorning().getBegin(), 
				periodProperties.getMorning().getFinish()
			);
		
		assertEquals(3, assemblyLines.get(0).getSteps().size());
		assertEquals(1, steps.size());
	}
	
	@Test
	public void shouldReturnListStepsWithDurationEqualToPeriod() {
		List<Step> steps = new ArrayList<>();
		LocalTime timeBegin = LocalTime.of(1, 0);
		LocalTime timeFinish = LocalTime.of(1, 30);
		Integer totalPeriod = 30;
		
		
		steps.add(Step.builder().timeInMinutes(10).build());
		steps.add(Step.builder().timeInMinutes(2).build());
		steps.add(Step.builder().timeInMinutes(10).build());
		steps.add(Step.builder().timeInMinutes(10).build());
		
		List<Step> periodSteps = service.createStepsRecursive(steps, timeBegin, timeFinish);
		Integer total = service.getTotalMinutes(periodSteps);
		
		assertEquals(totalPeriod, total);
	}
	
	@Test
	public void shouldReturnListStepsWithDurationEqualTo20() {
		List<Step> steps = new ArrayList<>();
		LocalTime timeBegin = LocalTime.of(1, 0);
		LocalTime timeFinish = LocalTime.of(1, 21);
		Integer totalPeriod = 20;
		
		
		steps.add(Step.builder().timeInMinutes(15).build());
		steps.add(Step.builder().timeInMinutes(2).build());
		steps.add(Step.builder().timeInMinutes(35).build());
		steps.add(Step.builder().timeInMinutes(20).build());
		
		List<Step> periodSteps = service.createStepsRecursive(steps, timeBegin, timeFinish);
		Integer total = service.getTotalMinutes(periodSteps);
		
		assertEquals(totalPeriod, total);
	}
}
