package facade;

import entity.Child;
import entity.Parent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class Facade {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("pu");
    public void create(Parent p)
    {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(p);
            p.getChildren().forEach(child -> {
                if (child.getId() == null)// tjekker om der findes allerede en child med det id
                    em.persist(child);
                if (em.find(Child.class, child.getId())==null)//Tjekker om denne child findes allerede i databasen
                    em.persist(child);
            });
            em.getTransaction().commit();
        }finally {
            em.close();
        }
    }

    public static void main(String[] args) {
        Facade facade = new Facade();
        Parent parent = new Parent("Helga",23);
        EntityManager entityManager = emf.createEntityManager();
        TypedQuery<Child> typedQuery = entityManager.createQuery("SELECT c FROM Child c WHERE c.name=:name", Child.class);
        typedQuery.setParameter("name","Ole");
        List<Child> children = typedQuery.getResultList();
        parent.addChild(children.get(0));
        //Child child = new Child("Ole",22);
        //parent.addChild(child);

        facade.create(parent);
    }
}
