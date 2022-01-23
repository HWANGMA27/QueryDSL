package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberTeamDTO {
    private Long memberId;
    private String username;
    private int age;
    private Long teamId;
    private String teamName;

    public MemberTeamDTO() {
    }

    @QueryProjection
    public MemberTeamDTO(Long memberId, String username, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.username = username;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
