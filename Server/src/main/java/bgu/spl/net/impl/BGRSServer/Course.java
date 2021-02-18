package bgu.spl.net.impl.BGRSServer;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Course {
    private int courseNum;
    private String courseName;
    private LinkedList<Integer> kdam;
    private LinkedList<Student> students;
    int index;
    private int numMax;

    public Course(int courseNum, String courseName, LinkedList<Integer> kdam, int numMax, int index) {
        this.courseName = courseName;
        this.courseNum = courseNum;
        this.kdam = kdam;
        this.numMax = numMax;
        this.index = index;
		students=new LinkedList<>();
    }

    	public synchronized void addStudent(Student s) {						// synchronized in case a few clients try to register at the same time
		if (!students.contains(s)) {
			students.add(s);
		}
	}
    public String courseStat() {
        String output = "Course: (" + courseNum + ") " + courseName+"\n";
        output = output+"Seats Available: " + (numMax - students.size()) + "/" + numMax+"\n";
        output = output+"Students Registered: [";
        if (!students.isEmpty()) {
            Collections.sort(students, Comparator.comparing(Student::getUserName));
            for (Student s : students) {
                String name = s.getUserName();
                output = output + name + ",";
            }
            output = output.substring(0, output.length() - 1);
        }
        output = output+"]";
        return output;
    }

    public int getIndex() {
        return index;
    }

    public String getKdamString() {
        String output = "[";
        for (Integer i : kdam)
            output = output + i + ",";
        if(output.length()>1)
            output = output.substring(0, output.length() - 1);
        output = output + "]";
        return output;
    }

    public LinkedList<Student> getStudents(){
        return students;
    }

    public synchronized boolean canRegister(Student st){						// checks if a student has the needed kdam courses and if there 
        boolean canReg = false;																	// synchronized so the result will be accurate 
        LinkedList<Integer> stKdam = st.getSortedCourses();
        int left = numMax-students.size();
        if(!st.getIsAdmin()&&stKdam.containsAll(kdam)&&left>0)
            canReg = true;
        return canReg;
    }
}
