package jpabook.jpashop;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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
            //데이터 중심적인 설계
            Order order = em.find(Order.class, 1L);
            Long memberId = order.getMemberId();
            em.find(Member.class, memberId);

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
