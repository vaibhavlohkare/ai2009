package friends.database;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Reviews database table.
 * 
 */
@Entity
@Table(name="Reviews")
public class Review implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int reviewID;
	
	private int linkID;

	private String reviewRatings;

	private String reviews;

	private String userID;

    public Review() {
    }

    
    public int getReviewID() {
		return this.reviewID;
	}

	public void setReviewID(int reviewID) {
		this.reviewID = reviewID;
	}
    
	public int getLinkID() {
		return this.linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	public String getReviewRatings() {
		return this.reviewRatings;
	}

	public void setReviewRatings(String reviewRatings) {
		this.reviewRatings = reviewRatings;
	}

	public String getReviews() {
		return this.reviews;
	}

	public void setReviews(String reviews) {
		this.reviews = reviews;
	}

	public String getUserID() {
		return this.userID;
	}

	public void setUserID(String userID2) {
		this.userID = userID2;
	}

}