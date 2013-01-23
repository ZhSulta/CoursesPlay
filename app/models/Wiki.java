package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

@Entity
@Table(name = "Wikies")
public class Wiki extends Model{
	@ManyToOne
	public Course course;
	public String topic;	
	public String title;
	public String text;
	public int position;	
	public Date publish_date;
	public Date modified_date;
}
