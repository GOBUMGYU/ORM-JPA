# JPA 시작하기

### 메이븐 소개

- [https://maven.apache.org/](https://maven.apache.org/)
- 자바 라이브러리, 빌드 관리
- 라이브러리 자동 다운로드 및 의존성 관리
- 최근에는 그래들(Gradle)이 점점 유명

### 라이브러리 추가

```
<dependency>
		<groupId>javax.xml.bind</groupId>
		<artifactId>jaxb-api</artifactId>
		<version>2.3.0</version>
</dependency>
<!-- JPA하이버네이트-->
<dependency>
   <groupId>org.hibernate</groupId>
   <artifactId>hibernate-entitymanager</artifactId>
   <version>5.3.10.Final</version>
</dependency>
<!-- H2데이터베이스-->
<dependency>
   <groupId>com.h2database</groupId>
   <artifactId>h2</artifactId>
   <version>1.4.200</version>
</dependency>      
```

### JPA 설정하기 - persistence.xml

- **JPA설정 파일**
- **/META-INF/persistence.xml 위치**
- **persistence-unit name으로 이름 지정**
- **javax.persistence로 시작 : JPA표준 속성**
- **hibernate로 시작 : 하이버네이트 전용 속성**
    
    ```
    <?xml version="1.0" encoding="UTF-8" ?>
    <persistence version="2.2"
                 xmlns="http://xmlns.jcp.org/xml/ns/persistence"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_2.xsd">
        <persistence-unit name="hello">
            <properties>
                <!--필수 속성-->
                <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
                <property name="javax.persistence.jdbc.user" value="sa"/>
                <property name="javax.persistence.jdbc.password" value=""/>
                <property name="javax.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
                <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
    
                <!--옵션-->
                <property name="hibernate.show_sql" value="true"/>
                <property name="hibernate.format_sql" value="true"/>
                <property name="hibernate.use_sql_comments" value="true"/>
                <!--<property name="hibernate.hbm2ddl.auto" value="create"/>-->
             </properties>
        </persistence-unit>
    </persistence>
    ```
    

### 데이터베이스 방언

- **JPA는 특정 베이터베이스에 종속 X**
- **각각의 데이터베이스가 제공하는 SQL문법과 함수는 조금씩 다름**
    - **가변 문자 : MySQL은 VARCHAR, Oracle는 VARCHAR2**
    - **문자열을 자르는 함수 : SQL표준은 SUBSTRING(), Oracle는 SUBSTR()**
    - **페이징 : MySQL은 LIMIT, Oracle은 ROWNUM**
- **방언 : SQL표준을 지키지 않는 특정 데이터베이스만의 고유한 기능**  
![image](https://user-images.githubusercontent.com/106207558/220242506-268f073d-baa4-4a70-9253-0a34e9bfd261.png)  
- **hibernate.dialect 속성에 지정**
- **H2 : org.hibernate.dialect.H2Dialect**
- **Oracle 12g : org.hibernate.dialect.Oracle12gDialect**
- **MySQL : org.hibernate.dialect.MySQL5Dialect**
- **하이버네이트는 40가지 이상의 데이터베이스 방언 지원**

### JPA의 구동방식

![image](https://user-images.githubusercontent.com/106207558/220242613-bdc8669c-5bf6-41f5-acbe-87ea2006f821.png)  

### 실습 - JPA 동작 확인

- **JpaMain클래스**
- **JPA동작 확인**

```
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

```

### 객체와 테이블을 생성하고 매핑하기

- **@Entity: JPA가 관리할 객체**
- **@ID: 데이터베이스 PK와 매핑**

**create table Member (
   id bigint not null,
   name varchar(255),
   primary key(id)
);**

```
@Entity
public class Member {
    @Id
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

### 주의

- **엔티티 매니저 팩토리 하나만 생성해서 애플리케이션 전체에서 공유**
- **엔티티 매니저는 쓰레드간에 공유 X (사용하고 버려야 한다).**
- **JPA의 모든 데이터 변경은 트랙잭션 안에서 실행**

### **JPQL 소개**

- **가장 단순한 조회 방법**
    - **EntityManager.find()**
    - **객체 그래프 탐색(a.getB().getC())**
- **나이가 18살 이상인 회원을 모두 검색하고 싶다면?**

### **실습 - JPQL 소개**

- **JPQL로 전체 회원검색**

```
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

```

- **JPQL로 ID가 2 이상인 회원만 검색**
- **JPQL로 이름이 같은 회원만 검색**
- **JPQL에 대해 자세한 내용은 객체지향 쿼리에서 학습**

### **JPQL**

- **JPA를 사용하면 엔티티 객체를 중심으로 개발**
- **문제는 검색 쿼리**
- **검색을 할 때도 테이블이 아닌 엔티티 객체를 대상으로 검색**
- **모든 DB데이터를 객체로 변환해서 검색하는 것은 불가능**
- **애플리케이션이 필요한 데이터만 DB에서 불러오려면 결국 검색 조건이 포함된 SQL이 필요**

- **JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공**
- **SQL과 문법 유사, SELECT, FROM , WHERE, GROUP BY, HAVING, JOIN지원**
- **JPQL은 엔티티 객체를 대상으로 쿼리**
- **SQL은 데이터베이스 테이블을 대상으로 쿼리**

- **테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리**
- **SQL을 추상화해서 특정 데이터베이스 SQL에 의존 X**
- **JPQL을 한마디로 정의하면 객체 지향 SQL**
