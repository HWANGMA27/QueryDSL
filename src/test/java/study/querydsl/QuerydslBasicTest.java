package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {

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

    @Test
    public void startJPQL() throws Exception{
        //JPQL로 member1을 찾는다
        Member findByJpql = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        assertThat(findByJpql.getUsername()).isEqualTo("member1");
    }

    @Test
    public void startQuerydsl() throws Exception{
        //QueryDSL로 member1을 찾는다
        Member findByDSL = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        assertThat(findByDSL.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchTest() throws Exception{
        //given
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1").and(member.age.eq(10)))
                .fetchOne();
        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void searchWithParam() throws Exception{
        //given
        //where 기본이 and라 쉼표로 연결 가능하다.
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"),
                        member.age.eq(10))
                .fetchOne();
        //then
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void resultResult() {
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

//        Member fetchOne = queryFactory
//                .selectFrom(QMember.member)
//                .fetchOne();

        Member fetchFirst = queryFactory
                .selectFrom(QMember.member)
                .fetchFirst();

    }

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순 desc
     * 2. 회원 이름 올림차순 asc
     * 단 2에서 회원이름이 없으면 마지막에 출력
     */
    @Test
    public void sort() throws Exception{
        //given
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        //when
        List<Member> list = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();
        //then
        for (Member member : list) {
            System.out.println("member = " + member);
        }
    }

    /*
    페이징 처리
     */
    @Test
    public void paging() throws Exception{
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        Long cnt = queryFactory
                .select(member.count())
                .from(member)
                .fetchOne();

        assertThat(cnt).isEqualTo(4);

    }
}
