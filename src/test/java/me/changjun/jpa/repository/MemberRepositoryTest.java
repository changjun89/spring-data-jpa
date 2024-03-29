package me.changjun.jpa.repository;

import me.changjun.jpa.dto.MemberDto;
import me.changjun.jpa.entity.Member;
import me.changjun.jpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        System.out.println("MemberRepository : " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        //리스트 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThenTest() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testFindUserNameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUserNameList();
        assertThat(result.get(0)).isEqualTo("AAA");
        assertThat(result.get(1)).isEqualTo("BBB");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void testFindMemberDto() {
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);

        Team team = new Team("teamA");
        m1.setTeam(team);
        teamRepository.save(team);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getUsername()).isEqualTo("AAA");
        assertThat(memberDto.get(0).getTeamName()).isEqualTo("teamA");
        assertThat(memberDto.size()).isEqualTo(1);
    }

    @Test
    void testFindByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byNames = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        assertThat(byNames.get(0).getUsername()).isEqualTo("AAA");
        assertThat(byNames.get(1).getUsername()).isEqualTo("BBB");
        assertThat(byNames.size()).isEqualTo(2);
    }

    @Test
    void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> listMember = memberRepository.findListByUsername("AAA");
        Member member = memberRepository.findMemberByUsername("AAA");
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");

        assertThat(listMember.get(0).getUsername()).isEqualTo("AAA");
        assertThat(member.getUsername()).isEqualTo("AAA");
        assertThat(optionalMember.get().getUsername()).isEqualTo("AAA");
    }

    @Test
    void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Page<Member> byAge = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = byAge.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        assertThat(byAge.getContent().size()).isEqualTo(3);
        assertThat(byAge.getTotalElements()).isEqualTo(5);
        assertThat(byAge.getTotalPages()).isEqualTo(2);
        assertThat(byAge.isFirst()).isEqualTo(true);
        assertThat(byAge.hasNext()).isEqualTo(true);
    }

    @Test
    void pagingSlice() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        Slice<Member> byAge = memberRepository.findSliceByAge(age, pageRequest);

        assertThat(byAge.getContent().size()).isEqualTo(3);
        assertThat(byAge.isFirst()).isEqualTo(true);
        assertThat(byAge.hasNext()).isEqualTo(true);
    }

    @Test
    void bulkUpdate() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        int resultCount = memberRepository.bulkAgePlus(20);
        Member member5 = memberRepository.findListByUsername("member5").get(0);

        assertThat(resultCount).isEqualTo(3);
        assertThat(member5.getAge()).isEqualTo(41);
    }

    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        //when N+1
        //select Member 1
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    void lock() {
        //given
        Member member1 = memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    void callCustom() {
        List<Member> memberCustom = memberRepository.findMemberCustom();
    }

    @Test
    void specBasic() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member memberA = new Member("m1", 0, teamA);
        Member memberB = new Member("m2", 0, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        assertThat(result.size()).isEqualTo(1);
    }


    @Test
    void queryByExample() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member memberA = new Member("m1", 0, teamA);
        Member memberB = new Member("m2", 0, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        //probe
        Member member = new Member("m1");
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreCase("age", "team");
        Example<Member> example = Example.of(member, exampleMatcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void projections() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member memberA = new Member("m1", 0, teamA);
        Member memberB = new Member("m2", 0, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnlyDto usernameOnly : result) {
            System.out.println("useNameOnly=" + usernameOnly.getUsername());
        }
    }

    @Test
    void nativeQueryTest() {
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member memberA = new Member("m1", 0, teamA);
        Member memberB = new Member("m2", 0, teamA);
        em.persist(memberA);
        em.persist(memberB);

        em.flush();
        em.clear();

        Page<MemberProjection> byNativeProjection = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = byNativeProjection.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection team Name = " + memberProjection.getTeamName());
            System.out.println("memberProjection user name = " + memberProjection.getUserName());
        }
    }
}