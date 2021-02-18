package bgu.spl.net.impl.BGRSServer;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Passive object representing the bgu.spl.net.srv.Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
	private ConcurrentHashMap<String,Student> users;           //<userName, user>
	private ConcurrentHashMap<Integer,Course> courses;       //<courseNum, Course>
	private ConcurrentHashMap<String, Student> students;     //<userName, student>
	private Object lock=new Object();

	private static class DBHolder {
			private static Database db = new Database();
		}

	//to prevent user from creating new bgu.spl.net.srv.Database
	public Database(){
		courses = new ConcurrentHashMap<>();
		students = new ConcurrentHashMap<>();
		users = new ConcurrentHashMap<>();
		initialize("./Courses.txt");
	}
                  /**
	 * loades the courses from the file path specified 
	 * into the bgu.spl.net.srv.Database, returns true if successful.
	 */

	public static Database getInstance() {
		return DBHolder.db;
	}
	/**
	 * Retrieves the single instance of this class.
	 */

	LinkedList<Integer> splitKdam(String kdams){
		kdams = kdams.substring(1,kdams.length()-1);  //cut brackets []
		String[] newkdam = kdams.split(",");
		LinkedList<Integer> splits = new LinkedList<>();
		for(String s: newkdam) {
			if (!s.isEmpty()) {
				int foo = Integer.parseInt(s);  //covert coure string to num
				splits.add(foo);                      //and add to the new list
			}
		}
		return splits;
	}

	public ConcurrentHashMap<String, Student> getUsers(){
		return users;
	}
	public Student getUser(String userName){
		if (users.containsKey(userName)){
			return users.get(userName);
		}
		return null;
	}

	public boolean registerUser(String userName, String password, boolean isAdmin) {
		synchronized (lock) {
			if (users.containsKey(userName))
				return false;
			Student user = new Student(userName, password, isAdmin);
			users.put(userName, user);
			return true;
		}
	}



	public boolean registerCourse(Student st, int courseNum){
		boolean reg = false;
		if(st.getIsAdmin()||!courses.containsKey(courseNum)){  //err
			return false;
		}
		synchronized (courses.get(courseNum)) {
			if (courses.get(courseNum).canRegister(st)) {  //if qualified
				courses.get(courseNum).addStudent(st);
				st.addCourse(courses.get(courseNum).getIndex(), courseNum);
				reg = true;
			}
		}
		return reg;
	}

	public void unregisterStudent(Student st, int courseNum){
		synchronized (courses.get(courseNum)){
			courses.get(courseNum).getStudents().remove(st);
      		st.getRegisteredTo().remove(courses.get(courseNum).getIndex());
		}
	}
	public ConcurrentHashMap<Integer,Course> getCourses(){
		return courses;
	}

	boolean initialize(String coursesFilePath)  {
		int index=0; //index for ordering the courses by intilliazing order
		try(BufferedReader br = new BufferedReader(new FileReader(coursesFilePath))){ //read line
			String line;
			while((line = br.readLine())!=null){
				String[] parts = line.split("\\|");
				int first = Integer.parseInt(parts[0]);  
				int fourth = Integer.parseInt(parts[3]);
				courses.putIfAbsent(first,new Course(first,parts[1],splitKdam(parts[2]),fourth,index));
                                                                    //parts[0] is courseNum
                                                                    //parts[1] is courseName
                                                                    //parts[2] is kdamList
                                                                    //parts[3] is maxSeats
				index++;
			}
		} catch (IOException e) {
		}
	
		return true;
	}

}

