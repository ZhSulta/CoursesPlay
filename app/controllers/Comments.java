package controllers;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;
import play.mvc.With;
@With(Secure.class)
@Check("admin")
public class Comments extends CRUD{

}
