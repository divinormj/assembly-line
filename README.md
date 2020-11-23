# assembly-line
Neogrid

Problem Statement - Assembly Line

We need to adjust our assembly lines using the below constraints.
there are so many possibilities, you should write a program to organize this production.

Constraints:
- Don't use plugins utilities or frameworks for date calculations (e.g. Joda-Time)
- Use input.txt as the system input data file.
- The production has multiple assembly lines and each one has the morning, lunch and afternoon periods.
- Each period has multiple steps of the production process. Obviously the lunch period doesn't have them.
- The morning period begins at 9:00 am and must finish by 12:00 noon, for lunch.
- The afternoon period begins at 1:00 pm and must finish in time for the labor gymnastics activities.
- The labor gymnastics activities can start no earlier than 4:00 pm and no later than 5:00 pm.
- The production step titles don't have numbers in it.
- All the numbers in the production step titles are the step time in minutes or the word "maintenance" which one represents a 5 minutes of technical pause.
- It won't have interval between the process steps

Depending on how you choose to solve this issue the output may give a different ordering or combination of the process steps into the assembly lines. This is acceptable, there aren't a required order of them and the steps can be distributed into any assembly line. 
You don’t need to exactly produce the sample output given here, but you need to consider the constraints!

----------------------------------------------------------------------------------------------------------------------

The implementation was made using Spring Boot, Java 11.
To display the result, a screen was made using Thymeleaf (template/index.html).
The program also generates an output file.
The directories where the program searches for the input file (input.txt) and where it writes the output file must be defined in application.yml, in the properties challenge.storage.local.directory-input and challenge.storage.local.directory-ouput.
For tests (JUnit) the same properties must be changed in application-test.yml.
