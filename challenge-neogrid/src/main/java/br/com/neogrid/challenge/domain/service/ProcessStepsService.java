package br.com.neogrid.challenge.domain.service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import br.com.neogrid.challenge.core.storage.PeriodProperties;
import br.com.neogrid.challenge.domain.exception.ProcessException;
import br.com.neogrid.challenge.domain.model.AssemblyLine;
import br.com.neogrid.challenge.domain.model.Step;

/**
 * Processes the titles with the production steps applying the restrictions to generate the corresponding assembly lines.
 * @author Divino Martins
 *
 */
@Service
public class ProcessStepsService {
	
	public static final String STEP_TITLE_LABOR_GYM = "Labor gymnastics";
	public static final String STEP_TITLE_LUNCH = "Lanch";	
	
	@Autowired
	private PeriodProperties periodProperties;
	
	/**
	 * 
	 * @param titles
	 * @return the list of assembly lines, never null.
	 */
	public List<AssemblyLine> generateAssemblyLine(List<String> titles) {
		if(titles == null
				|| titles.isEmpty()) {
			throw new ProcessException("The tiles was not informed.");
		}
		
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		List<Step> steps = this.generateSteps(titles);
		
		try {
			assemblyLines = this.generateAssemblyLines(steps);
		} catch (Exception ex) {
			throw new ProcessException("It was not possible to generate the assembly lines", ex);
		}
		
		if(!steps.isEmpty()) {
			throw new ProcessException("There are steps that are longer than the length of the periods.");
		}
		
		return assemblyLines;
	}
	
	/**
	 * @return the total duration time in minutes of the morning.
	 */
	private Long getMorningPeriodLength() {
		return periodProperties.getMorning().getBegin()
				.until(periodProperties.getMorning().getFinish(), ChronoUnit.MINUTES);
	}
	
	/**
	 * @return the total duration time in minutes of the afternoon.
	 */
	private Long getAfternoonPeriodLength() {
		return periodProperties.getAfternoon().getBegin()
				.until(periodProperties.getAfternoon().getLaborGymMax(), ChronoUnit.MINUTES);
	}
	
	/**
	 * @return the total duration time in minutes of the lunch.
	 */
	private Long getLunchPeriodLength() {
		return periodProperties.getMorning().getFinish()
				.until(periodProperties.getAfternoon().getBegin(), ChronoUnit.MINUTES);
	}
	
	/**
	 * @return the total duration time in minutes of the lunch.
	 */
	private Long getTotalPeriodLength() {
		return this.getMorningPeriodLength() + this.getAfternoonPeriodLength();
	}
	
	public Integer countAssemblylines(List<Step> steps, Long totalPeriodLength) {
		Integer count = 0;
		
		try {
			Double totalTimeSteps = steps.stream().mapToDouble(step -> step.getTimeInMinutes()).sum();
			Double totalAssemblylines = Math.ceil(totalTimeSteps.doubleValue() / totalPeriodLength);
			
			count = totalAssemblylines.intValue();
		} catch (Exception ex) {}
		
		return count;
	}
	
	/**
	 * Generates steps with title and time in minutes.
	 * The list is returned in descending order of duration. 
	 * Constraint: The production step titles don't have numbers in it.
	 * 
	 * @param file
	 * @return the list of steps, never null.
	 */
	public List<Step> generateSteps(List<String> titles) {
		List<Step> steps = new ArrayList<>();
				
		if(titles != null) {
			for (String title : titles) {
				Step step = Step.builder()
						.title(title)
						.timeInMinutes(this.getStepDurationInMinutes(title))
						.build();
				steps.add(step);
			};
			
			steps.sort(Comparator.comparingInt(Step::getTimeInMinutes).reversed());
		}
				
		return steps;
	}
	
	/**
	 * Get the duration in minutes of the step.
	 * Constraint:
	 * All the numbers in the production step titles are the step time in minutes 
	 * or the word "maintenance" which one represents a 5 minutes of technical pause.
	 * For this restriction, it was ignored whether upper or lower case.
	 * @param title
	 * @return the step duration in minutes.
	 * @throws Exception 
	 */
	public Integer getStepDurationInMinutes(String title) {
		if(title == null) {
			throw new ProcessException("The title must be informed.");
		}
		
		String numbers = title.replaceAll("[^0-9]+", "");
		
		if(numbers.isBlank()) {
			if(title.toLowerCase().contains("maintenance")) {
				numbers = "5";
			}
			else {
				throw new ProcessException("The time in minutes for the step is missing.");
			}
		}
		
		return NumberUtils.parseNumber(numbers, Integer.class);
	}
	
	/**
	 * 
	 * Constraints:
	 * - The production has multiple assembly lines and each one has the morning, lunch and afternoon periods. 
	 * - It won't have interval between the process steps.
	 * @param steps
	 * @return
	 * @throws Exception 
	 */
	private List<AssemblyLine> generateAssemblyLines(List<Step> steps) {
		List<AssemblyLine> assemblyLines = this.createNewArrayListAssemblyLines(steps);
		
		this.addMorningConsumingStepsForAllAssemblyLines(assemblyLines, steps);
		this.addLunchToAssemblyLines(assemblyLines);
		this.addAfternoonConsumingStepsForAllAssemblyLines(assemblyLines, steps);
		this.addLaborGymnasticsToAssemblyLines(assemblyLines);
		
		return assemblyLines;
	}
	
	/**
	 * Create a list of assembly lines.
	 * The number of assembly lines is defined by the total time of the steps by the maximum number of hours worked per day.
	 * @return
	 */
	private List<AssemblyLine> createNewArrayListAssemblyLines(List<Step> steps) {
		List<AssemblyLine> assemblyLines = new ArrayList<>();
		Integer totalAssemblylines = this.countAssemblylines(steps, this.getTotalPeriodLength());
		Integer countAssemblyLine = 0;
		
		while(countAssemblyLine < totalAssemblylines) {			
			AssemblyLine assemblyLine = new AssemblyLine();
			
			countAssemblyLine++;
			
			assemblyLine.setNumber(countAssemblyLine);
			
			assemblyLines.add(assemblyLine);
		}
		
		return assemblyLines;
	}
	
	/**
	 * Adds lunch time to all assembly lines.
	 * @param assemblyLines
	 */
	public void addLunchToAssemblyLines(List<AssemblyLine> assemblyLines) {
		for (AssemblyLine assemblyLine : assemblyLines) {
			Step step = this.createStepLunch(assemblyLine);
			
			assemblyLine.getSteps().add(step);
		}
	}
	
	private Step createStepLunch(AssemblyLine assemblyLine) {
		Step step = Step.builder()
				.title(STEP_TITLE_LUNCH)
				.timeInMinutes(this.getLunchPeriodLength().intValue())
				.time(periodProperties.getMorning().getFinish())
				.build();
		
		return step;
	}
	
	/**
	 * Adds labor gymnastics to all assembly lines.
	 * Constraints: The labor gymnastics activities can start no earlier than 4:00 pm and no later than 5:00 pm.
	 * @param assemblyLines
	 * @throws Exception 
	 */
	public void addLaborGymnasticsToAssemblyLines(List<AssemblyLine> assemblyLines) {
		for (AssemblyLine assemblyLine : assemblyLines) {
			Step step = this.createStepLaborGymnastics(assemblyLine);
			
			assemblyLine.getSteps().add(step);
		}
	}
	
	/**
	 * Creates the stage of labor gymnastics.
	 * @param assemblyLine
	 * @return  
	 */
	private Step createStepLaborGymnastics(AssemblyLine assemblyLine) {
		LocalTime time = this.getTimeLaborGymnastics(assemblyLine);
		Step step = Step.builder()
						.time(time)
						.title(STEP_TITLE_LABOR_GYM)
						.build();
		
		return step;
	}
	
	/**
	 * Get the start time of gymnastics at work. 
	 * Constraints: The labor gymnastics activities can start no earlier than 4:00 pm and no later than 5:00 pm.
	 * @param assemblyLine
	 * @return the start time of gymnastics at work.
	 */
	public LocalTime getTimeLaborGymnastics(AssemblyLine assemblyLine) {
		LocalTime time = null;
		Step last = null;
		
		if(assemblyLine != null) {
			last = this.getLastStep(assemblyLine.getSteps());
		}
		
		if(last != null
				&& last.getTimeInMinutes() != null) {
			time = last.getTime().plusMinutes(last.getTimeInMinutes());
		}
		
		if(time == null
				|| periodProperties.getAfternoon().getLaborGymMin().isAfter(time)) {
			time = periodProperties.getAfternoon().getLaborGymMin();
		}
		
		return time;
	}
	
	/**
	 * Adds the morning period for all assembly lines.
	 * The steps included in the assembly line are removed from the steps list.
	 * @param assemblyLines
	 * @param steps
	 */
	private void addMorningConsumingStepsForAllAssemblyLines(List<AssemblyLine> assemblyLines, List<Step> steps) {
		this.addAndConsumingStepsForAllAssemblyLines(
				assemblyLines, 
				steps, 
				periodProperties.getMorning().getBegin(), 
				periodProperties.getMorning().getFinish()
			);
	}
		
	/**
	 * Adds the afternoon period for all assembly lines.
	 * The steps included in the assembly line are removed from the steps list.
	 * @param assemblyLines
	 * @param steps
	 */
	private void addAfternoonConsumingStepsForAllAssemblyLines(List<AssemblyLine> assemblyLines, List<Step> steps) {
		this.addAndConsumingStepsForAllAssemblyLines(
				assemblyLines, 
				steps, 
				periodProperties.getAfternoon().getBegin(), 
				periodProperties.getAfternoon().getLaborGymMax()
			);
	}
	
	/**
	 * Adds steps for all assembly lines.
	 * The steps included in the assembly line are removed from the steps list.
	 * @param assemblyLines
	 * @param steps
	 */
	public void addAndConsumingStepsForAllAssemblyLines(
			List<AssemblyLine> assemblyLines, 
			List<Step> steps,
			LocalTime timeBegin, 
			LocalTime timeFinish) {
		for (AssemblyLine assemblyLine : assemblyLines) {
			List<Step> periodSteps = this.createStepsRecursive(steps, timeBegin, timeFinish);
			
			assemblyLine.getSteps().addAll(periodSteps);
			steps.removeAll(periodSteps);
		}
	}
	
	/**
	 * 
	 * @param stepsA
	 * @param stepsB
	 * @return the list with the steps that has the longest duration.
	 */
	private List<Step> maxStepsMinutes(List<Step> stepsA, List<Step> stepsB) {
		int totalA = getTotalMinutes(stepsA);
		int totalB = getTotalMinutes(stepsB);
		
		if(totalA > totalB) {
			return stepsA;
		}
		
		return stepsB;
	}
	
	/**
	 * 
	 * @param steps
	 * @return the total in minutes to perform all steps.
	 */
	public Integer getTotalMinutes(List<Step> steps) {
		return steps == null ? 0 : steps.stream().mapToInt(step -> step.getTimeInMinutes()).sum();
	}

	/**
	 * 
	 * @param steps
	 * @return the next time to start the step, consider the last step of the list plus the time to perform that step.
	 */
	private LocalTime getNextTime(List<Step> steps) {
		Step step = this.getLastStep(steps);
		
		if(step != null) {
			return step.getTime().plusMinutes(step.getTimeInMinutes());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param assemblyLine
	 * @return the last step of the assembly line.
	 */
	public Step getLastStep(List<Step> steps) {
		return steps.stream()
				.max(Comparator.comparing(Step::getTime))
				.orElse(null);
	}

	/**
	 * Go through the list of process steps that have not yet been distributed on any assembly lines.
	 * Checking all combinations until you find one that has exactly the same duration as the period or the longest found.
	 * @param steps
	 * @param periodLength
	 * @param time
	 * @return the list steps, never null.
	 */
	public List<Step> createStepsRecursive(List<Step> steps, LocalTime timeBegin, LocalTime timeFinish) {
		List<Step> periodStepsMax = new ArrayList<>();
		
		if(steps != null && timeBegin != null && timeFinish != null) {
			List<Step> recursive = null;
			List<Step> periodSteps = null;
			LocalTime time = null;
			//Indicates the beginning of the list for recursion.
			//The element of this index remains fixed until it is tested with all combinations.
			int indexStart = 0;
			int indexRemove = 0;
			int max = 0;
			int total = 0;
			
			do {
				recursive = new ArrayList<>();
				
				if(indexStart < steps.size()) {
					recursive = steps.subList(indexStart, steps.size()).stream().collect(Collectors.toList());
					//When starting a new combination the total duration of the steps must be recalculated.
					if(indexRemove == 0) {
						total = this.getTotalMinutes(recursive);
					}
				}
				
				if(indexRemove > 0
						&& indexRemove < recursive.size()) {
					//Removes an element from the list to perform calculations with a new combination.
					recursive.remove(indexRemove);
				}
				
				periodSteps = this.createSteps(recursive, timeBegin, timeFinish);
				
				periodStepsMax = this.maxStepsMinutes(periodSteps, periodStepsMax);
				
				max = this.getTotalMinutes(periodStepsMax);
				if(max == total) {
					break;
				}
				
				time = this.getNextTime(periodSteps);
				if(timeFinish.equals(time)) {
					break;
				}
								
				indexRemove++;
					
				if(indexRemove >= (steps.size()-1)) {
					indexRemove = 0;
					indexStart++;
				}
				
			} while(!recursive.isEmpty());	
		}		
		
		return periodStepsMax;
	}
	
	/**
	 * Go through the list of process steps that have not yet been distributed on any assembly lines
	 * and perform the calculations with the duration of the step,
	 * generating a new list of steps that have a total duration less than or equal to the period.
	 * @param steps
	 * @param periodLength
	 * @param time
	 * @return the steps from period, never null.
	 */
	private List<Step> createSteps(List<Step> steps, LocalTime timeBegin, LocalTime timeFinish) {
		List<Step> periodSteps = new ArrayList<>();
		
		if(steps != null && timeBegin != null && timeFinish != null) {
			LocalTime time = timeBegin;
		
			for (Step step : steps) {
				if(timeFinish.compareTo(time.plusMinutes(step.getTimeInMinutes())) >= 0) {				 
					 step.setTime(time);
					 time = time.plusMinutes(step.getTimeInMinutes());
					 
					 periodSteps.add(step);
					 
					 if(time.equals(timeFinish)) {
						 break;
					 }
				 } 
			}
		}
		
		return periodSteps;
	}
		
}
