package me.changjun.jpa.repository;

import me.changjun.jpa.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberCustom();
}
