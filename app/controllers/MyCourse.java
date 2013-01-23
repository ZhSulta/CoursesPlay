package controllers;

import play.*;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.mvc.*;
 
import java.io.File;
import java.util.*;
 
import models.*;

@With(Secure.class)
public class MyCourse extends Controller{
	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) {        	
        	User user = Cache.get("user",User.class);
        	if(user==null){
        		user = User.getUserByEmail(session.get("email"));
        		Cache.set("user", user,"30mn");
        	}
            renderArgs.put("user", user);            
        }
    }
    public static void courseForm() {
    	render();
    }
    
    public static void userPhoto(long id) {
    	   final Course course = Course.findById(id);
    	   notFoundIfNull(course);    	   
    	   response.setContentTypeIfNotSet(course.photo.type());
    	   renderBinary(course.photo.get());
    }

    public static void checkCourse(long courseId) {
    	User user = (User)renderArgs.get("user");
    	
    	List<Course> myCourses = Course.getMyOwnCourses(user);
//    	System.out.println(myCourses.size());
    	for(int i=0;i<myCourses.size();i++){    		
//    		System.out.println(myCourses.get(i).id);
    		if(myCourses.get(i).id == courseId){    			
    			MyOwnCourse.myOwnCourse(courseId,null);
    			break;
    		}
    	}
    	myCourse(courseId);
    }
    public static void myCourse(long courseId) {
    	System.out.println(courseId);
    	Course course = Cache.get("course_"+courseId,Course.class);
    	System.out.println(course);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	boolean enrolled = false;
    	User user = (User)renderArgs.get("user");
    	models.MyCourse c = models.MyCourse.getMyCourseByCourseAndUser(course, user);
    	if(c != null){
    		enrolled = true;
    	}
    	System.out.println("enrolled " + enrolled);
    	render(enrolled,courseId);
    }
    public static void enroll(long courseId){
    	System.out.println("asdasd     "+courseId);
    	User user = (User)renderArgs.get("user");
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	course = Course.getCourseById(courseId);
    	models.MyCourse myCourse = new models.MyCourse(user, course);
    	myCourse.save();
    	myCourse(courseId);
    }
//    public static void myOwnCourse(long courseId) {
//    	Course course = Cache.get("course_"+courseId,Course.class);
//    	System.out.println(course);
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set("course_"+courseId, course,"30mn");
//    	}
//    	List<Video> videos = Video.getMyOwnVideos(course);
//    	render("MyOwnCourse/myOwnCourse.html",courseId,videos);
//    }
//    public static void addVideo(String topic, long courseId, String name, String url) {
//    	System.out.println(courseId+" courseId");
//    	Date date = new Date();
//    	int position = 1;
//    	Course course = Cache.get("course_"+courseId,Course.class);
//    	Video video = new Video(course, topic, position, name, date, url);
//    	video.save();
//    	myOwnCourse(courseId);
//    }
    public static void createCourse(String courseName,String smallDescription,String description,String university,
    		int duration, Blob photo) {
    	Date startDate = new Date();
    	User user = (User)renderArgs.get("user");
    	String resourses = "";
    	Course course = new Course(courseName, university, duration, smallDescription, description, startDate, user, resourses, photo);
    	course.save();
    	
    	System.out.println(user);

    	MyOwnCourse.myOwnCourse(course.id,null);
    }
    public static void courseInfo(){
    	render();
    }
    public static void syllabus(){
    	render();
    }
    public static void tutorial_resourses(){
    	render();
    }
    public static void wiki(){
    	render();
    }
    public static void discussion(){
    	render();
    }
    public static void progress(){
    	render();
    }
}
