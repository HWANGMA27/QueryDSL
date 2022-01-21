package study.querydsl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDTO;
import study.querydsl.dto.QMemberDTO;
import study.querydsl.dto.UserDTO;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static study.querydsl.entity.QMember.member;

@SpringBootTest
@Transactional
public class QuerydslProjectionTest {

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
        프로젝션으로 setter로 DTO 바로 원하는 필드만 반환받기
         */
    @Test
    public void findDtoBySetter() throws Exception{
        List<MemberDTO> result = queryFactory
                .select(Projections.bean(MemberDTO.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO);
        }
    }
    /*
    프로젝션 field로 dto 원하는 필드만 반환받기
     */
    @Test
    public void findDtoByField() throws Exception{
        List<MemberDTO> result = queryFactory
                .select(Projections.fields(MemberDTO.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();
        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO);
        }
    }

    /*
    프로젝션 dto 생성자로 원하는 필드만 반환받기
     */
    @Test
    public void findDtoByConstructor() throws Exception{
        QMember memberSub = new QMember("memberSub");

        List<UserDTO> result = queryFactory
                .select(Projections.constructor(UserDTO.class,
                        member.username.as("name"),
                        ExpressionUtils.as(JPAExpressions
                                .select(memberSub.age.max())
                                .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        for (UserDTO userDTO : result) {
            System.out.println("userDTO = " + userDTO);
        }
    }

    /*
    constructor에 QueryProjection 설정 -> Q파일을 생성하여 사용
    장점 : 컴파일 오류로 체크할 수 있다.
    단점 : dto에 queryDsl에 대한 의존성을 갖게됨
    dto는 여러 레이어에 거쳐서 사용이 되는데, 구조적인 관점에서 dto가 복잡해지는 문제가 있다.
     */
    @Test
    public void findDtoByQueryProjection() throws Exception{
        List<MemberDTO> result = queryFactory
                .select(new QMemberDTO(member.username, member.age))
                .from(member)
                .fetch();
        //then
        for (MemberDTO memberDTO : result) {
            System.out.println(memberDTO);
        }

    }
}
