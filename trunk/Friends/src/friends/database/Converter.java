package friends.database;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import friends.database.*;

public class Converter {

	public Converter() {

	}

	public static void main(String[] args) throws Exception {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory(
				"testdbproj", System.getProperties());
		EntityManager em = factory.createEntityManager();


		em.getTransaction().begin();
		Query q = em.createQuery("SELECT u FROM Friends u");
		List<Friends> fr = q.getResultList();
		em.getTransaction().commit();

		InsertDataHelper iD = new InsertDataHelper();
		for (int x = 0; x < fr.size(); x++) {
			iD.insertUserFriendData(fr.get(x).getUserID(), fr.get(x)
					.getFriendID());
		}

		em.getTransaction().begin();
		Query q2 = em.createQuery("SELECT a FROM Review a");
		List<Review> re = q2.getResultList();
		em.getTransaction().commit();

		// InsertDataHelper iD2 = new InsertDataHelper();
		for (int x = 0; x < re.size(); x++) {
			String rating = re.get(x).getReviewRatings();
			if (rating.trim().length() > 0) {
				int r = Integer.parseInt(rating.trim());
				iD.insertReviewData(r, re.get(x).getUserID(), re.get(x)
						.getReviews());
			}
		}

		em.close();
	

	}

}
