package study.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QuerydslBulkTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void beforeEach(){
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    /*
    변경감지로 처리하면 쿼리가 너무 많이 나가게 되는 경우
    벌크연산을 실행한다.
     */
    @Test
    public void bulkUpdate1() throws Exception{
        //member1 = 10 -> 비회원
        //member1 = 20 -> 비회원
        //member1 = 30 -> 유지
        //member1 = 40 -> 유지
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28))
                .execute();
        em.flush();
        em.clear();
        List<Member> memberList = queryFactory
                .selectFrom(member)
                .fetch();
        //then
        for (Member member1 : memberList) {
            System.out.println(member1);
        }
    }
    /*
    모든 회원의 나이를 1살 더한다.
     */
    @Test
    public void bulkUpdate2() throws Exception{
        //given
        queryFactory
                .update(member)
                .set(member.age, member.age.add(1))
                .execute();

        em.flush();
        em.clear();
    }
    /*
    모든 회원의 나이를 2배로 변경한다.
     */
    @Test
    public void bulkUpdate3() throws Exception{
        queryFactory
                .update(member)
                .set(member.age, member.age.multiply(2))
                .execute();

        em.flush();
        em.clear();
    }
    /*
    나이가 28세 이상인 회원은 삭제한다.
     */
    @Test
    public void bulkDelte() throws Exception{
        queryFactory
                .delete(member)
                .where(member.age.gt(28))
                .execute();

        em.flush();
        em.clear();
    }
}
