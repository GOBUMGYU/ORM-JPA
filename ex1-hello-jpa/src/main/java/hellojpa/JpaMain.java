package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        //persistence -> EntityManagerFactory -> EntityManager
        //EntityManagerFactory 는 로딩 시점에 한 개만 생성 되어야 한다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        //행위가 있을 때 마다 EntityManager를 만들어줘야 한다.
        EntityManager em = emf.createEntityManager();

        //데이터가 변경되는 단위는 트랜잭션을 꼭 해주어야 한다.
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            //jpql
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(5)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }

            /**
             * 생성
             * Member member = new Member();
             * member.setId(1L);
             * member.setName("HelloA");
             * em.persist(member);
             */

             /**
             * 조회
             * Member findMember = em.find(Member.class, 1L);
             * System.out.println("findMember.getId() = " + findMember.getId());
             * System.out.println("findMember.getName() = " + findMember.getName());
             */

            /**
             * 삭제
             * Member findMember = em.find(Member.class, 1L);
             * em.remove(findMember);
             */

            /**
             * 수정
             * Member findMember = em.find(Member.class, 2L);
             * findMember.setName("HelloC");
             */

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        //code
        emf.close();
    }
}
