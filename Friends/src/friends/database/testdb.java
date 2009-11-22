package friends.database;

import javax.persistence.*;

public class testdb {

	public static void main(String args[]) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("testdbproj", System.getProperties());

		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();

		Link l = (Link) em.find(Link.class, 1);

		l.setLink("cnn.com");
		
		em.getTransaction().commit();

		em.close();
		
		System.out.println("Done.");
	}
}
