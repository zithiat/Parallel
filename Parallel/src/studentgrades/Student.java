package studentgrades;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Student {

	private String studentId;
	private String studentName;
	private String departmentId;
	private double gpa;
	private List<StudentGrade> gradeList;

	public Student(String studentId, String studentName, String departmentId) {
		super();
		this.studentId = studentId;
		this.studentName = studentName;
		this.departmentId = departmentId;
		this.gradeList = new ArrayList<StudentGrade>();
	}

	public String getStudentId() {
		return studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public double getGpa() {
		return gpa;
	}

	public void addStudentGrade(StudentGrade sg) {
		this.gradeList.add(sg);
	}
	
	public List<StudentGrade> getStudentGrade() {
		return this.gradeList;
	}

	public double computeGpa() {
		this.gpa = this.gradeList.stream().mapToDouble(g -> g.getCredits() * g.getGrade()).reduce(0.0, (a, b) -> a + b)
				/ this.gradeList.stream().mapToInt(c -> c.getCredits()).reduce(0, (a, b) -> a + b);
		return this.gpa;
	}

	@Override
	public String toString() {
		return "Student [studentId=" + studentId + ", studentName=" + studentName + ", departmentId=" + departmentId
				+ ", gradeList=\n\t" + gradeList.stream().map(a -> a.toString()).collect(Collectors.joining("\n\t")) + "\n]";
	}
}
