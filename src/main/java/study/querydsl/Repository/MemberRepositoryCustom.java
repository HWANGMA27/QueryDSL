package study.querydsl.Repository;

import org.springframework.data.domain.Pageable;
import study.querydsl.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> searchPageSimple(Pageable pageable);
    List<Member> searchPageComplex(Pageable pageable);
}
