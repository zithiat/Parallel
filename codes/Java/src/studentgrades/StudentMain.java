package studentgrades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.*;

public class StudentMain {

	public static void main(String[] args) {
		int numStudents = 1000000;
		int numCourses = 5;
		int numDepts = 5;
		long regTime, parTime;
		System.out.println(String.format("Total students: %,3d", numStudents));
		List<Student> studentList = new ArrayList<Student>();
		for (int i = 0; i < numStudents; i++) {
			String sid = String.format("%06d", i);
			String sname = "TestStudent" + sid;
			String dept = "Dept_" + ThreadLocalRandom.current().nextInt(1, numDepts);
			Student s = new Student(sid, sname, dept);
			for (int j = 0; j < numCourses; j++) {
				s.addStudentGrade(new StudentGrade(sid,
						"Course_" + ThreadLocalRandom.current().nextInt(1, numCourses * 2), LocalDate.now(),
						ThreadLocalRandom.current().nextInt(2, 4), ThreadLocalRandom.current().nextDouble(2.0, 4.0)));
			}
			studentList.add(s);
		}
		System.out.println("Generating done!");
		System.out.println("\nUsing regular stream");
		long start = System.currentTimeMillis();
		double averageGpa = studentList.stream().mapToDouble(g -> g.computeGpa()).average().getAsDouble();
		System.out.println(String.format("Average GPA of all students: %.3f", averageGpa));
		double lowestGpa = studentList.stream().mapToDouble(g -> g.computeGpa()).min().getAsDouble();
		System.out.println(String.format("Lowest GPA of all students: %.3f", lowestGpa));
		double highestGpa = studentList.stream().mapToDouble(g -> g.computeGpa()).max().getAsDouble();
		System.out.println(String.format("Highest GPA of all students: %.3f", highestGpa));
		studentList.stream().collect(groupingBy(Student::getDepartmentId, averagingDouble(s -> s.computeGpa())))
				.entrySet().stream().forEach(s -> System.out
						.println("Average GPA of " + s.getKey() + String.format(": %.3f", s.getValue())));
		studentList.stream().flatMap(s -> s.getStudentGrade().stream())
				.collect(groupingBy(StudentGrade::getCourseNumber)).entrySet().stream()
				.forEach(s -> System.out.println(String.format("Average GPA of %s is: %.3f", s.getKey(),
						s.getValue().stream().mapToDouble(m -> m.getCredits() * m.getGrade()).sum()
								/ s.getValue().stream().mapToInt(m -> m.getCredits()).sum())));
		regTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Time elapsed: %d ms", regTime));

		System.out.println("\nUsing parallel stream");
		start = System.currentTimeMillis();
		System.out.println(String.format("Average GPA of all students: %.3f",
				studentList.parallelStream().mapToDouble(g -> g.computeGpa()).average().getAsDouble()));
		System.out.println(String.format("Lowest GPA of all students: %.3f",
				studentList.parallelStream().mapToDouble(g -> g.computeGpa()).min().getAsDouble()));
		System.out.println(String.format("Highest GPA of all students: %.3f",
				studentList.parallelStream().mapToDouble(g -> g.computeGpa()).max().getAsDouble()));
		studentList.parallelStream().collect(groupingBy(Student::getDepartmentId, averagingDouble(s -> s.computeGpa())))
				.entrySet().parallelStream().forEach(s -> System.out
						.println("Average GPA of " + s.getKey() + String.format(": %.3f", s.getValue())));
		studentList.parallelStream().flatMap(s -> s.getStudentGrade().parallelStream())
				.collect(groupingBy(StudentGrade::getCourseNumber)).entrySet().parallelStream()
				.forEach(s -> System.out.println(String.format("Average GPA of %s is: %.3f", s.getKey(),
						s.getValue().parallelStream().mapToDouble(m -> m.getCredits() * m.getGrade()).sum()
								/ s.getValue().parallelStream().mapToInt(m -> m.getCredits()).sum())));
		parTime = System.currentTimeMillis() - start;
		System.out.println(String.format("Time elapsed: %d ms", parTime));
		System.out.println(String.format("Speedup: %.4f ms", (double) regTime / parTime));
	}
}
