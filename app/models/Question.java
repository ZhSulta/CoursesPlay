package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Questions")
public class Question extends Model{
	@ManyToOne
	public Course course;
	@ManyToOne
	public User user;
	public String title;
	@Lob
	public String text;
	public String tags;
	public int votes_number;
	public int answers_number;
	public int views_number;
	public Date publish_date;
	@OneToMany(mappedBy="question", cascade=CascadeType.ALL)
	public Set<Answer> answers;
	
	public Question(Course course, User user, String title, String text,
			String tags) {		
		this.course = course;
		this.user = user;
		this.title = title;
		this.text = text;
		this.tags = tags;
		this.votes_number = 0;
		this.answers_number = 0;
		this.views_number = 0;
		this.publish_date = new Date();
	}
	public static List<Question> getCourseQuestions(Course course){
		 return Question.find("byCourse", course).fetch();		 
	}
	public static List<Question> getUserQuestions(User user){
		 return Question.find("byUser", user).fetch();		 
	}
	public static Question getQuestionById(long id){
		 return Question.findById(id);		 
	}
}
