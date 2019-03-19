package studentgrades;

import java.time.LocalDate;

public class StudentGrade {
	private String studentId;
	
	private String courseNumber;
	private LocalDate courseDate;
	private int credits;
	private double grade;

	public StudentGrade(String studentId, String courseNumber, LocalDate courseDate, int credits,
			double grade) {
		super();
		this.studentId = studentId;
		this.courseNumber = courseNumber;
		this.courseDate = courseDate;
		this.credits = credits;
		this.grade = grade;
	}

	public String getStudentId() {
		return studentId;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public LocalDate getCourseDate() {
		return courseDate;
	}

	public int getCredits() {
		return credits;
	}

	public double getGrade() {
		return grade;
	}

	@Override
	public String toString() {
		return "StudentGrade [studentId=" + studentId + ", courseNumber=" + courseNumber + ", courseDate=" + courseDate
				+ ", credits=" + credits + ", grade=" + grade + "]";
	}
}
