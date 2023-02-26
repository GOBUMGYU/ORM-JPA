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
            //저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("memberA");
            em.persist(member);

            team.addMember(member);

            em.flush();
            em.clear();

            Team findTeam = em.find(Team.class, team.getId());
            List<Member> members = findTeam.getMembers();
            for(Member m : members) {
                System.out.println("m = " + m.getUsername());
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
