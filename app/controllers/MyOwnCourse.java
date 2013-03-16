package controllers;

import play.*;
import play.cache.Cache;
import play.db.jpa.Blob;
import play.modules.paginate.ValuePaginator;
import play.mvc.*;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import notifiers.Mails;
 
import models.*;

@With(Secure.class)
public class MyOwnCourse extends Controller{
	@Before
    static void setConnectedUser() {
        if(Security.isConnected()) { 
        	User user = Cache.get(session.getId() + "-user",User.class);
        	if(user==null){
        		user = User.getUserByEmail(session.get("email"));
        		Cache.set(session.getId() + "-user", user, "30mn");
        	}        	
            renderArgs.put("user", user);            
        }
    }	
    public static void courseForm() {
    	render();
    }
    public static void checkCourse(long courseId) {
//    	User user = (User)renderArgs.get("user");
//    	
//    	List<Course> myCourses = Course.getMyOwnCourses(user);
////    	System.out.println(myCourses.size());
//    	for(int i=0;i<myCourses.size();i++){    		
////    		System.out.println(myCourses.get(i).id);
//    		if(myCourses.get(i).id == courseId){    			
//    			myOwnCourse(courseId,0);
//    			break;
//    		}
//    	}
//    	myCourse(courseId);
    	User user = (User)renderArgs.get("user");
    	System.out.println(user);
    	List<Course> myCourses = Course.getMyOwnCourses(user);
    	for(int i=0;i<myCourses.size();i++){    		
    		System.out.println(myCourses.get(i).name);    		
    	}
//    	System.out.println(myCourses.size());
    	for(int i=0;i<myCourses.size();i++){    		
    		System.out.println(myCourses.get(i).id);
    		if(myCourses.get(i).id == courseId){    			
    			MyOwnCourse.myOwnCourse(courseId,0);
    			break;
    		}
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	boolean enrolled = false;
    	models.MyCourse c = models.MyCourse.getMyCourseByCourseAndUser(course, user);
    	if(c != null){
    		enrolled = true;
    	}
    	if(enrolled){
    		myOwnCourse(courseId,0);
    	}else{
    		welcomeToCourse(courseId);
    	}
    }
    public static void welcomeToCourse(long courseId) {
    	System.out.println(courseId);
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
//    public static void myCourse(long courseId) {    	
//    	System.out.println(courseId);
//    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
//    	if(course==null){
//    		course = Course.getCourseById(courseId);    		
//    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
//    	}
//    	render(course);
//    }
    public static void myOwnCourse(long courseId, long videoId) {
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	ArrayList<Video> videos = (ArrayList<Video>) Video.getMyOwnVideos(course);
    	Video video = null;
    	if(videoId == 0){
    		if(videos.size()>0){
    			video = videos.get(0);
    		}
    	}else{
    		video = Video.findById(videoId);
    	}
    	render(course,videos,video);
    }
    public static void enroll(long courseId){
    	User user = (User)renderArgs.get("user");
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
//    	course = Course.getCourseById(courseId);
    	models.MyCourse myCourse = new models.MyCourse(user, course);
    	myCourse.save();
    	myOwnCourse(courseId,0);
    }
    public static void activateCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.isActive = true;
    	course.save();
    	Application.manageCourses();
    }
    public static void deactivateCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.isActive = false;
    	course.save();
    	Application.manageCourses();
    }
    public static void deleteCourse(long courseId){
    	Course course = Course.findById(courseId);
    	course.delete();
    	Application.manageCourses();
    }
    public static void sendMessageToCourseOwner(long courseId,String email,String username){
    	Mails.correctCourse(email,username);
    	Application.manageCourses();
    }
    public static void createCourse(String courseName,String smallDescription,String description,String university,
    		int duration, Blob photo,String videoUrl) {
    	System.out.println(videoUrl.indexOf("watch"));
    	if(videoUrl.contains("watch")){
    		videoUrl = videoUrl.substring(0,videoUrl.indexOf("watch"))+"embed/"+videoUrl.substring(videoUrl.indexOf("=")+1);
    	}
    	videoUrl+="?autoplay=0&amp;color=red&amp;html5=1&amp;rel=0&amp;showinfo=0&amp;theme=light&amp;wmode=opaque&amp;enablejsapi=1&amp;";    	
    	Date startDate = new Date();
    	User user = (User)renderArgs.get("user");
    	String resourses = "";
    	Course course = new Course(courseName, university, duration, smallDescription, description, startDate, user, resourses, photo,videoUrl);
    	course.save();
    	
    	System.out.println(user);

    	MyOwnCourse.myOwnCourse(course.id,0);
    }
    public static void addVideo(String topic, long courseId, String name, String url) {
    	System.out.println(url.indexOf("watch"));
    	if(url.contains("watch")){
    		url = url.substring(0,url.indexOf("watch"))+"embed/"+url.substring(url.indexOf("=")+1);
    	}
    	url+="?autoplay=0&amp;color=red&amp;html5=1&amp;rel=0&amp;showinfo=0&amp;theme=light&amp;wmode=opaque&amp;enablejsapi=1&amp;";
    	System.out.println(url);
    	System.out.println(courseId+" courseId");
    	Date date = new Date();
    	int position = 1;
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	Video video = new Video(course, topic, position, name, date, url);
    	video.save();
    	myOwnCourse(courseId,0);
    }   
    public static void removeVideo(long courseId, String videoUrl) {
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
//    	Video video = Video.getBy
    	ArrayList<Video> videos = (ArrayList<Video>) Video.getMyOwnVideos(course);
		if(videos.size()>0){
			videoUrl = videos.get(0).getUrl();
		}    
    	render(course,videos,videoUrl);
    }
    public static void courseInfo(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	ArrayList<Announcement> announcements = (ArrayList<Announcement>) Announcement.getCourseAnnouncements(course);
//    	for(int i=0;i<announcements.size();i++){
//    		System.out.println(announcements.get(i).text);
//    	}
    	render(course,announcements);
    }
    
    public static void code(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	
    	render(course);
    }
    
    public static void addCourseInfo(Long id,String courseInfo, String topic,long courseId) {
    	System.out.println("id         sd   "+id);
    	Announcement announcement;
    	if(id==null){
    		Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
        	if(course==null){
        		course = Course.getCourseById(courseId);    		
        		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
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
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
    public static void editSyllabus(long courseId,String syllabus){    	
    	Course course = Course.getCourseById(courseId); 
    	course.syllabus = syllabus;    	
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	syllabus(courseId);
    }
    public static void tutorials_resourses(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	System.out.println(course);
    	render(course);
    }
    public static void myOwnCourses() {
    	User user = (User)renderArgs.get("user");
    	List<Course> myCourses = Course.getMyOwnCourses(user);
    	render(myCourses);
    }
    public static void myCourses() {
    	User user = (User)renderArgs.get("user");
    	List<MyCourse> myCourses = MyCourse.getMyCoursesByUser(user);
    	render(myCourses);
    }
    public static void editResourses(long courseId,String resources){    	
    	Course course = Course.getCourseById(courseId); 
    	course.resources = resources;    	
    	course.save();
    	Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	tutorials_resourses(courseId);
    }
    public static void assignments(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	List<Assignment> assignments = Assignment.getCourseAssignments(course);
    	Set<String> topics = new HashSet<String>();
    	for(int i=0;i<assignments.size();i++){
    		topics.add(assignments.get(i).topic);    		
    	}
    	System.out.println(topics.size());
    	render(course,assignments,topics);
    }
    public static void addAssignment(long courseId,String title, String topic,int attempts,String due_date){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	int position = 0;
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy"); 
    	Date convertedDueDate = null;
		try {
			convertedDueDate = dateFormat.parse(due_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date hard_deadline = convertedDueDate;
    	Assignment assignment = new Assignment(course, topic, title, convertedDueDate, hard_deadline, position, attempts);
    	assignment.save();
    	assignments(courseId);
    }
    public static void assignment(long courseId,long assignmentId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    	
    	Assignment assignment = Assignment.findById(assignmentId);
    	List<AssignmentQuestion> questions = AssignmentQuestion.getAssignmentQuestions(assignment);
    	System.out.println(questions.size());
//    	for(int i=0;i<questions.size();i++){
//    		System.out.println(questions.get(i).answers.size());
//    	}
//    	List<AssignmentAnswer> answers = AssignmentAnswer.getAssignmentQuestionAnswers(question);
    	render(course,assignment,questions);
    }
    public static void addAssignmentQuestion(long courseId,long assignmentId,String title, String text,int score,String explanation){
    	int position = 0;
    	Assignment assignment = Assignment.findById(assignmentId);
    	int oneAnswer = 0;
    	AssignmentQuestion question = new AssignmentQuestion(assignment, title, text, position, score, explanation,oneAnswer);
    	question.save();
    	assignment(courseId,assignmentId);
    }
    public static void addAssignmentAnswer(long assignmentAnswerId,long courseId,long assignmentId,long questionId,
    		String text, boolean correctness){
    	System.out.println(assignmentAnswerId);
    	if(assignmentAnswerId==0){
    		AssignmentQuestion question = AssignmentQuestion.findById(questionId);
    		if(correctness){
    			question.oneAnswer++;
    		}
    		question.save();
        	AssignmentAnswer answer = new AssignmentAnswer(question, text, correctness);
        	answer.save();
    	}else{
    		AssignmentQuestion question = AssignmentQuestion.findById(questionId);
    		AssignmentAnswer answer = AssignmentAnswer.findById(assignmentAnswerId);
    		if(correctness&&!answer.correctness){
    			question.oneAnswer++;
    		}else if(!correctness&&answer.correctness){
    			question.oneAnswer--;
    		}
    		
    		question.save();
    		answer.text = text;
    		answer.correctness = correctness;
    		answer.save();
    	}
    	assignment(courseId,assignmentId);
    }
    public static void deleteAssignmentAnswer(long courseId,long assignmentId,long questionId, long answerId){
    	AssignmentAnswer answer = AssignmentAnswer.findById(answerId);
    	if(answer.correctness){
    		AssignmentQuestion assignmentQuestion = AssignmentQuestion.findById(questionId);    		
    		assignmentQuestion.oneAnswer--;
    		assignmentQuestion.save();
    	}    	
    	answer.delete();
    	assignment(courseId,assignmentId);
    }
    
    public static void checkAssignment(Long courseId,Long assignmentId,Long answer[]){  
    	System.out.println();
    	Date dateOfAttempt = new Date();
    	User user = Cache.get(session.getId() + "-user",User.class);
    	Assignment assignment = Assignment.findById(assignmentId);
    	int score = 0;
    	int totalScore = 0;
    	UserAssignment userAssignment = new UserAssignment(assignment, user, dateOfAttempt, score);
//    	AssignmentQuestion[] assignmentQuestions = (AssignmentQuestion[]) assignment.questions.toArray();
//    	for(int i=0;i<assignmentQuestions.length;i++){
//    		totalScore+=assignmentQuestions[i].score;
//    	}
    	for (AssignmentQuestion assignmentQuestion : assignment.questions){
    		totalScore+=assignmentQuestion.score;
    	}
    	UserAssignmentAnswer userAssignmentAnswer;
    	UserAssignmentAnswer userAssignmentAnswer1[] = null;
    	if(answer!=null){
    		userAssignmentAnswer1 = new UserAssignmentAnswer[answer.length];
    		for(int i=0;i<answer.length;i++){
	    		System.out.print(answer[i]+" ");
	    		AssignmentAnswer answer1 = AssignmentAnswer.findById(answer[i]);
	    		if(answer1.correctness){
	    			if(answer1.question.oneAnswer!=0){
	    				score+=answer1.question.score/answer1.question.oneAnswer;
	    			}	    			
	    		}
	        	userAssignmentAnswer = new UserAssignmentAnswer(userAssignment, answer1);
	        	userAssignmentAnswer1[i] = userAssignmentAnswer;	    
	        	userAssignment.userAssignmentAnswers.add(userAssignmentAnswer);
	    	}
    	}
    	System.out.println("score is "+score);
    	userAssignment.score = score;
    	userAssignment.save();
    	if(userAssignmentAnswer1!=null){
    		for(int i=0;i<userAssignmentAnswer1.length;i++){
    			userAssignmentAnswer1[i].save();
    		}
    	}
    	for(int i=0;i<userAssignment.userAssignmentAnswers.size();i++){
    		System.out.println(userAssignment.userAssignmentAnswers.size());
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_" + courseId, course,"30mn");
    	}    
    	List<AssignmentQuestion> questions = AssignmentQuestion.getAssignmentQuestions(assignment);
    	List<UserAssignmentAnswer> userAssignmentAnswers = UserAssignmentAnswer.getUserAssignmentAnswers(userAssignment);
    	render("MyOwnCourse/assignmentFeedback.html",course,assignment,userAssignment,questions,userAssignmentAnswers,totalScore);

    	
    	
    	
//    	    	assignment(courseId,assignmentId);
//    	assignmentFeedback(courseId, assignmentId,userAssignment);
    }
    
    public static void discussion(long courseId,String search){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
//    	List<Question> questions = null;
//    	if(sortBy==null||sortBy.equals("active")){
//    		questions = Question.getCourseQuestions(course,"active");
//    	}else if(sortBy.equals("new")){
//    		questions = Question.getCourseQuestions(course,"new");
//    	}else{
//    		questions = Question.getCourseQuestions(course,"vote");
//    	}
    	List<Question> aQuestions = Question.getCourseQuestions(course,"active");
    	List<Question> nQuestions = Question.getCourseQuestions(course,"new");
    	List<Question> vQuestions = Question.getCourseQuestions(course,"vote");
    	
    	ValuePaginator vpaginator = new ValuePaginator(aQuestions);    	
    	vpaginator.setPageSize(2);
    	vpaginator.setBoundaryControlsEnabled(true);
    	vpaginator.setPagesDisplayed(0);
    	System.out.println(vpaginator.getPagesDisplayed());
    	renderArgs.put("aQuestions", vpaginator);
    	renderArgs.put("page", vpaginator.getPageNumber());
//    	
//    	vpaginator = new ValuePaginator(nQuestions);    	
//    	vpaginator.setPageSize(2);
//    	vpaginator.setBoundaryControlsEnabled(true);
//    	vpaginator.setPagesDisplayed(1);
//    	renderArgs.put("nQuestions", vpaginator);
//    	
//    	vpaginator = new ValuePaginator(vQuestions);    	
//    	vpaginator.setPageSize(2);
//    	vpaginator.setBoundaryControlsEnabled(true);
//    	vpaginator.setPagesDisplayed(1);    	
//    	renderArgs.put("vQuestions", vpaginator);
    	if(search==null){
    		search = "acitve";
    	}
    	render(course,search);    	
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
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	User user = (User)renderArgs.get("user");
    	Question question = new Question(course, user, title, text, tags);
    	question.save();
    	discussion(courseId,null);
    }
    public static void deleteQuestion(long courseId,long questionId){
    	Question question = Question.getQuestionById(questionId);
    	question.delete();    	
    	discussion(courseId,null);
    }
    public static void question(long courseId,long questionId){
    	Question question = Question.getQuestionById(questionId);
    	if(session.get("question_id-"+questionId)==null){
	    	session.put("question_id-"+questionId, questionId);
	    	question.views_number = question.views_number+1;
    	}
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	question.save();
    	List<Answer> answers = Answer.getAnswers(question);
    	render(course,question,answers);
    }
    public static void addAnswer(long courseId,long questionId,String text){
    	User user = (User)renderArgs.get("user");
    	Question question = Question.getQuestionById(questionId);
    	question.answers_number = question.answers_number+1;
    	question.save();
    	Answer answer = new Answer(question, user, text);
    	answer.save();
    	question(courseId,questionId);    	
    }
    public static void deleteAnswer(long courseId,long questionId,long answerId){
    	Answer answer = Answer.findById(answerId);
    	answer.delete();    	
    	question(courseId,questionId);
    }
    public static void progress(long courseId){
    	Course course = Cache.get(session.getId() + "_user-course_"+courseId,Course.class);    	
    	if(course==null){
    		course = Course.getCourseById(courseId);    		
    		Cache.set(session.getId() + "_user-course_"+courseId, course,"30mn");
    	}
    	render(course);
    }
}
