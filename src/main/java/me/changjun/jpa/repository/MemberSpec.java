package me.changjun.jpa.repository;

import me.changjun.jpa.entity.Member;
import me.changjun.jpa.entity.Team;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public class MemberSpec {

    public static Specification<Member> teamName(final String name) {
        return (Specification<Member>) (root, query, criteriaBuilder) -> {
            if (StringUtils.isEmpty(name)) {
                return null;
            }
            Join<Member, Team> team = root.join("team", JoinType.INNER);
            return criteriaBuilder.equal(team.get("name"), name);
        };
    }

    public static Specification<Member> username(final String username) {
        return (Specification<Member>) (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("username"), username);
    }
}
