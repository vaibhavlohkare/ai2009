package friends.database;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Links database table.
 * 
 */
@Entity
@Table(name="Links")
public class Link implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int linkID;
	
	private String link;

	public Link() {
    }

	public int getLinkID() {
		return this.linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	public String getLink() {
		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}