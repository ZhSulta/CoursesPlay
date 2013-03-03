package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.db.jpa.JPABase;
import play.i18n.Lang;
import play.mvc.*;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import notifiers.Mails;

import models.*;
import play.*;
 

public class Application extends Controller {
    
	public static void index() {
//		Mails.welcome();
		System.out.println(Security.isConnected());
		List<Course> myCourses = new ArrayList<Course>();
		User user = null;
		if(Security.isConnected()) {
			user = Cache.get(session.getId() + "-user",User.class);
        	if(user==null){
        		user = User.getUserByEmail(session.get("email"));
        		Cache.set(session.getId() + "-user", user, "30mn");
        	}
        	myCourses = Course.getMyOwnCourses(user);
        }
		
		for(int i=0;i<myCourses.size();i++){
			System.out.println(myCourses.get(i));
		}
		List<Course> universityCourses = Course.getUniversityCourses(user);		
		List<Course> userCourses = Course.getUserCourses(user);
		
		render(universityCourses,userCourses,user);
    }
	public static void manageCourses() {
		List<Course> activeCourses = Course.getActiveCourses();
		List<Course> notActiveCourses = Course.getNotActiveCourses();
		List<Course> allCourses = Course.findAll();		
		User user = Cache.get(session.getId() + "-user",User.class);
    	if(user==null){
    		user = User.getUserByEmail(session.get("email"));
    		Cache.set(session.getId() + "-user", user, "30mn");
    	}
    	render(activeCourses,notActiveCourses,allCourses,user);
    }   
    public static void signup() {    	
    	render();
    } 
    public static void coursePhoto(long id) {
    	   final Course course = Course.findById(id);
    	   notFoundIfNull(course);    	   
    	   System.out.println(id+"a111111111111");
    	   response.setContentTypeIfNotSet(course.photo.type());
    	   renderBinary(course.photo.get());
    }
    public static void save(Long id,
    		@Required(message="Email is required") String email, 
    		@Required(message="Password is required") String pwd, 
    		@Required(message="Password confirmation is required") String cpwd) throws Throwable {
    	
    	System.out.println(email);
    	System.out.println(pwd);
    	System.out.println(cpwd);
    	
    	if(!pwd.equals(cpwd)){
    		flash.keep("url");
            flash.error("passwords are not equal");
            params.flash();
            signup();
    	}
    	User user1 = User.getUserByEmail(email);
        if(user1!=null){
        	flash.keep("url");
            flash.error("username with this name is already exists");
            params.flash();
            signup();
    	}

        
    	validation.valid(user1);
        if(validation.hasErrors()){
        	render("Application/signup.html",user1);
        }
        User user = new User(email,pwd);
        // Save
        user.save();
//    	Mails.welcome(user);
    	Secure.login();
    	
    }
    public static void changeLanguage(String lang) {    
    	System.out.println(lang);
    	Lang.change(lang);		
    	profile();
    }
    public static void profile() {
    	User user = Cache.get(session.getId() + "-user",User.class);
    	if(user==null){
    		user = User.getUserByEmail(session.get("email"));
    		Cache.set(session.getId() + "-user", user, "30mn");
    	}
    	List<models.MyCourse> myCourses = models.MyCourse.getMyCoursesByUser(user);
    	List<Course> myOwnCourses = Course.getMyOwnCourses(user);
    	List<Question> questions = Question.getUserQuestions(user);
    	render(user,myCourses,myOwnCourses,questions);
    }
    public static void editAccount() {
    	User user = Cache.get(session.getId() + "-user",User.class);
    	if(user==null){
    		user = User.getUserByEmail(session.get("email"));
    		Cache.set(session.getId() + "-user", user, "30mn");
    	}
    	user = User.getUserByEmail(session.get("email"));
    	DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    	String birthday = formatter.format(new Date());
    	if(user.birthday!=null){    		
    		birthday = formatter.format(user.birthday);
    	}
    	System.out.println(user);
    	render(user,birthday);
    }
    public static void userPhoto(long id) {
 	   final User user = User.findById(id);
 	   notFoundIfNull(user);    	   
 	   response.setContentTypeIfNotSet(user.avatar.type());
 	   renderBinary(user.avatar.get());
 }
    public static void saveProfile(String username,String firstName, String lastName,String gender,
    		String birthday,String location, String aboutMe, Blob avatar) {
//    	User user = Cache.get(session.getId() + "-user",User.class);
//    	if(user==null){
//    		user = User.getUserByEmail(session.get("email"));
//    		Cache.set(session.getId() + "-user", user, "30mn");
//    	}    	
    	User user = User.getUserByEmail(session.get("email"));
    	System.out.println(avatar);
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy"); 
    	Date convertedDate = null;
		try {
			convertedDate = dateFormat.parse(birthday);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		User user1 = new User(user.email, user.pwd,avatar);
//		user1.save();
		
    	user.username = username;
    	user.firstName = firstName;
    	user.lastName = lastName;
    	user.gender = gender;
    	user.birthday = convertedDate;
    	user.location = location;
    	user.aboutMe = aboutMe;
    	user.avatar = avatar;
    	user.save();    	
    	editAccount();
    }
    public static void help() {
    	render();
    }
}