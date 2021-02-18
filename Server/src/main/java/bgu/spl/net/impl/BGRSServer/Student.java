package bgu.spl.net.impl.BGRSServer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class Student {

    private String userName;
    private String password;
    private boolean isAdmin;
    boolean loggedIn = false;
    private ConcurrentHashMap<Integer,Integer> registeredTo;  //<index,courseNum>
    
    public Student(String s1, String s2, boolean b) {
        userName=s1;
        password=s2;
        isAdmin=b;
        registeredTo = new ConcurrentHashMap<>();
    }
    public synchronized boolean isLoggedIn() {
        return loggedIn;
    }
    public String getPassword() {
        return password;
    }
    public String getUserName() {
        return userName;
    }
    public LinkedList getSortedCourses(){						// for STUDENTSTAT. Returns a list of the numbers of the courses the student is registered to. Sorted by index
        LinkedList<Integer> indexes = new LinkedList<>();
        for (Integer i: registeredTo.keySet()) {
            indexes.add(i);
        }
        Collections.sort(indexes);
        LinkedList<Integer> sorted = new LinkedList<>();
        for (int i: indexes) {
            sorted.add(registeredTo.get(i));
        }
        return sorted;
    }

    public void addCourse(int index, int courseNum) {
        registeredTo.putIfAbsent(index,courseNum);
    }

    public boolean getIsAdmin(){
        return isAdmin;
    }
    public synchronized void logIn() {							// synchronized so a few clients won't be able to log in to the same user at the same time
        loggedIn=true;
    }

    public synchronized void logOut() {
        loggedIn=false;
    }

    public String getStats() {						// output of STUDENTSTAT

        String output = "Student: "+userName+"\n"+"Courses: [";
        LinkedList<Integer> sorted = getSortedCourses();
        if (!sorted.isEmpty()) {
            for (Integer courseNum : sorted) {
                output=output+courseNum+",";
            }
            output=output.substring(0,output.length()-1);
        }
        output = output+"]";
        return output;
    }

    public ConcurrentHashMap<Integer,Integer> getRegisteredTo(){
        return registeredTo;
    }

}
