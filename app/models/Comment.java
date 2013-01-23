package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Comments")
public class Comment extends Model{
	
	@ManyToOne
	public Answer answer;
	@ManyToOne
	public User user;
	public String text;
	public Date date;
}
