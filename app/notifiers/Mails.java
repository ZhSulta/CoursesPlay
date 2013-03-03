package notifiers;
 
import play.*;
import play.mvc.*;
import java.util.*;

import org.apache.commons.mail.*;

import models.User;
 
public class Mails extends Mailer {
	public static String projectName = "Online Courses";
	public static String projectSite = "onlinecourses.kz";
	public static void welcome() {
	      setSubject("Welcome %s", "sultan");
	      addRecipient("zh.sulta@gmail.com");
	      setFrom("Me <me@me.com>");
	      String username = "Alish";
	      send(username);
	   }
	public static void correctCourse(String email,String username) {
		setSubject("Notification: %s", projectName);
		String pName = projectName;
		String pSite = projectSite;
		addRecipient(email);
        setFrom("Me <me@me.com>");
		if(username!=null&&username.length()>=0){
			send(username,pName,pSite);
		}else{
			username = email;			
			send(username,pName,pSite);
		}
        
	}
//   public static void welcome(User user) {
//      setSubject("Welcome %s", user.username);
//      addRecipient(user.email);
//      setFrom("Me <me@me.com>");
//      EmailAttachment attachment = new EmailAttachment();
//      attachment.setDescription("A pdf document");
//      attachment.setPath(Play.getFile("rules.pdf").getPath());
//      addAttachment(attachment);
//      send(user);
//   }
 
   public static void lostPassword(User user) {
      String newpassword = user.pwd;
      setFrom("Robot <robot@thecompany.com>");
      setSubject("Your password has been reset");
      addRecipient(user.email);
      send(user, newpassword);
   }
 
}