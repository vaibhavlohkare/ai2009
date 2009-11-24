package friends.database;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Links database table.
 * 
 */
@Entity
@Table(name="Friends")
public class Friends implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	private String userID;
	
	private String friendID;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getFriendID() {
		return friendID;
	}

	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}
	
}
