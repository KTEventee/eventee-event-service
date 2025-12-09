package eventee.server.event.domain.infrastructure.client.member;

public interface MemberQueryPort {
    MemberListDto.MemberDto getMember(Long memberId);
}