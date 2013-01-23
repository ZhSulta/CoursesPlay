package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;


@Entity
@Table(name="COURSES")
public class Course extends Model{	

	public String name;
	public String university;
	public int duration;
	public String smallDescription;
	public String description;
	public Blob photo;
	@Lob
    @Required
	public String syllabus;
	@Lob
    public String resources;
	
	public Date date;
	public Date startDate;
	public boolean isActive;
	@ManyToOne
	public User user;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL)
	public Set<Announcement> announcements;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL)
	public Set<Video> videos;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL)
	public Set<Question> questions;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL)
	public Set<Wiki> wikies;
	@OneToMany(mappedBy="course", cascade=CascadeType.ALL)
	public Set<MyCourse> myCourses;
	
	public Course(String name, String university, int duration,
			String smallDescription, String description,
			Date startDate, User user,String resourses, Blob photo) {
		this.name = name;
		isActive = false;
		this.university = university;
		this.duration = duration;
		this.resources = resourses;
		this.smallDescription = smallDescription;
		this.description = description;
		this.photo = photo;
		this.date = new Date();
		this.startDate = startDate;
		this.user = user;
		this.announcements = new HashSet<Announcement>();
		this.videos = new HashSet<Video>();
		this.questions = new HashSet<Question>();
		this.wikies = new HashSet<Wiki>();
		this.myCourses = new HashSet<MyCourse>();
	}
	public static List<Course> getMyOwnCourses(User user){
		 List<Course> courses = Course.find("byUser", user).fetch();
		 return courses;
	}
	public static Course getCourseById(long courseId){
		 return Course.findById(courseId);	 
	}
	public String toString() {
	    return name;
	}
}
