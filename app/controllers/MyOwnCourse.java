package controllers;

import play.*;
import play.cache.Cache;
import play.mvc.*;
 
import java.util.*;
 
import models.*;

@With(Secure.class)
public class MyOwnCourse extends Controller{
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
    public static void checkCourse(long courseId) {
    	User user = (User)renderArgs.get("user");
    	
    	List<Course> myCourses = Course.getMyOwnCourses(user);
//    	System.out.println(myCourses.size());
    	for(int i=0;i<myCourses.size();i++){    		
//    		System.out.println(myCourses.get(i).id);
    		if(myCourses.get(i).id == courseId){    			
    			myOwnCourse(courseId,null);
    			break;
    		}
    	}
    	myCourse(courseId);
    }
    public static void myCourse(long courseId) {
    	Course course = Cache.get("course_"+courseId,Course.class);
    	System.out.println(course);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	render();
    }
    public static void addVideo(String topic, long courseId, String name, String url) {
    	System.out.println(url.indexOf("watch"));
    	if(url.contains("watch")){
    		url = url.substring(0,url.indexOf("watch"))+"embed/"+url.substring(url.indexOf("=")+1);
    	}
    	System.out.println(url);
    	System.out.println(courseId+" courseId");
    	Date date = new Date();
    	int position = 1;
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	Video video = new Video(course, topic, position, name, date, url);
    	video.save();
    	myOwnCourse(courseId,null);
    }
    public static void myOwnCourse(long courseId, String videoUrl) {
    	Course course = Cache.get("course_"+courseId,Course.class);
    	System.out.println(course);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	ArrayList<Video> videos = (ArrayList<Video>) Video.getMyOwnVideos(course);
    	if(videoUrl == null){
    		if(videos.size()>0){
    			videoUrl = videos.get(0).getUrl();
    		}
    	}
    	render(courseId,videos,videoUrl);
    }
    public static void courseInfo(long courseId){
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	ArrayList<Announcement> announcements = (ArrayList<Announcement>) Announcement.getCourseAnnouncements(course);
//    	for(int i=0;i<announcements.size();i++){
//    		System.out.println(announcements.get(i).text);
//    	}
    	render(courseId,announcements);
    }
    public static void addCourseInfo(Long id,String courseInfo, String topic,long courseId) {
    	System.out.println("id         sd   "+id);
    	Announcement announcement;
    	if(id==null){
    		Course course = Cache.get("course_"+courseId,Course.class);
        	if(course==null){
        		course = Course.getCourseById(courseId);    		
        		Cache.set("course_"+courseId, course,"30mn");
        	}
    		announcement = new Announcement(course, topic, courseInfo);
    	}else{
    		announcement = Announcement.findById(id);
    		announcement.topic = topic;
    		announcement.text = courseInfo;    		
    	}
    	
    	announcement.save();    	
    	//System.out.println(courseInfo);
    	courseInfo(courseId);
    }
    public static void deleteCourseInfo(long courseId,long announcementId){
    	System.out.println(courseId);
    	System.out.println(announcementId);
    	Announcement.deleteAnnouncement(announcementId);
    	courseInfo(courseId);
    }
    public static void syllabus(long courseId){
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	render(courseId,course);
    }
    public static void editSyllabus(long courseId,String syllabus){
    	
    	Course course = Course.getCourseById(courseId); 
    	course.syllabus = syllabus;    	
    	course.save();
    	Cache.set("course_"+courseId, course,"30mn");
    	syllabus(courseId);
    }
    public static void tutorials_resourses(long courseId){
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	render(courseId,course);
    }
    public static void myOwnCourses() {
    	User user = (User)renderArgs.get("user");
    	List<Course> myCourses = Course.getMyOwnCourses(user);
    	render(myCourses);
    }
    public static void editResourses(long courseId,String resources){    	
    	Course course = Course.getCourseById(courseId); 
    	course.resources = resources;    	
    	course.save();
    	Cache.set("course_"+courseId, course,"30mn");
    	tutorials_resourses(courseId);
    }
    public static void wiki(long courseId){
    	render(courseId);
    }
    public static void discussion(long courseId,String sortBy){
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	List<Question> questions = null;
    	if(sortBy==null){
    		questions = Question.getCourseQuestions(course);
    	}else if(sortBy.equals("new")){
    		questions = Question.getCourseQuestions(course);
    	}else if(sortBy.equals("vote")){
    		questions = Question.getCourseQuestions(course);
    	}else{
    		questions = Question.getCourseQuestions(course);
    	}
    	render(courseId,questions);
    }
    public static void listNewQuestions() {
    	List<Question> questions = Question.findAll();
    	for(int i=0;i<questions.size();i++){
    		System.out.println(questions.get(i).title);
    	}
        renderJSON(questions);
    }
    public static void addQuestion(long courseId,String title,String text){
    	String tags = "";
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	User user = (User)renderArgs.get("user");
    	Question question = new Question(course, user, title, text, tags);
    	question.save();
    	discussion(courseId,null);
    }
    public static void question(long courseId,long questionId){
    	Question question = Question.getQuestionById(questionId);
    	question.views_number = question.views_number+1;
    	question.save();
    	List<Answer> answers = Answer.getAnswers(question);
    	render(courseId,question,questionId,answers);
    }
    public static void addAnswer(long courseId,long questionId,String text){
    	User user = (User)renderArgs.get("user");
    	Question question = Question.getQuestionById(questionId);
    	question.answers_number = question.answers_number++;
    	question.save();
    	Answer answer = new Answer(question, user, text);
    	answer.save();
    	question(courseId,questionId);    	
    }
    public static void progress(long courseId){
    	Course course = Cache.get("course_"+courseId,Course.class);
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set("course_"+courseId, course,"30mn");
    	}
    	List<models.MyCourse> myCourses = models.MyCourse.getMyCourses(course);
    	render(courseId,myCourses);
    }
}
