package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDTO;
import study.querydsl.dto.QMemberDTO;
import study.querydsl.dto.UserDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslDynamicQueryTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void beforeEach() {
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
    복수의 검색조건이 있을 때
    BooleanBuilder를 활용해 값이 있는 것들만 검색조건으로 입력하는 방법
     */
    @Test
    public void dynamicQuery_BooleanBuilder() throws Exception{
        //given
        String usernameParam = "member1";
        Integer ageParam = null;

        //when

        List<Member> result = searchMember1(usernameParam, ageParam);
        //then

        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember1(String usernameCon, Integer ageCon) {
        BooleanBuilder builder = new BooleanBuilder();
        if(usernameCon != null){
            builder.and(member.username.eq(usernameCon));
        }
        if(ageCon != null){
            builder.and(member.age.eq(ageCon));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    /*
    복수의 검색조건이 있을 때
     */
    @Test
    public void dynamicQuery_WhereParam() throws Exception{
        //given
        String usernameParam = "member1";
        Integer ageParam = null;

        //when

        List<Member> result = searchMember2(usernameParam, ageParam);
        //then

        assertThat(result.size()).isEqualTo(1);
    }

    private List<Member> searchMember2(String usernameCond, Integer ageCond) {
        return queryFactory
                .selectFrom(member)
                .where(alLEq(usernameCond, ageCond))
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond == null ? null : member.username.eq(usernameCond);
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond == null ? null : member.age.eq(ageCond);
    }

    private BooleanExpression alLEq(String usernameCond, Integer ageCond){
        return usernameEq(usernameCond).and(ageEq(ageCond));
    }
}