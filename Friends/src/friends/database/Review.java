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
	private int linkID;

	private int reviewerID;

	private String reviewRatings;

	private String reviews;

	private int userID;

    public Review() {
    }

	public int getLinkID() {
		return this.linkID;
	}

	public void setLinkID(int linkID) {
		this.linkID = linkID;
	}

	public int getReviewerID() {
		return this.reviewerID;
	}

	public void setReviewerID(int reviewerID) {
		this.reviewerID = reviewerID;
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

	public int getUserID() {
		return this.userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

}