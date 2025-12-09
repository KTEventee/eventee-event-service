package eventee.server.event.domain.event.service;

import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventRequest.UpdateRequest;
import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.event.dto.MemberListDto;
import eventee.server.event.domain.member.model.Member;

import java.util.List;

public interface EventService {

  EventResponse.CreateResponse createEvent(Member member, EventRequest.CreateRequest request);

  EventResponse.JoinResponse joinEvent(Member member, EventRequest.JoinRequest inviteCode);
  EventResponse.EventWithGroupsResponse getEventGroups(Member member, Long eventId);
  EventResponse.GroupPostsResponse getGroupPosts(Member member, Long eventId, Long groupId);

  EventResponse.InviteCodeValidateResponse validateInviteCode(String code);

  EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request);

  List<MemberListDto.MemberDto> getMembersByEvent(long eventId, Member member);
  void kickMember(EventRequest.KickMemberRequest request, Member member);

  EventResponse.UpdateEventResponse updateEventInfo(UpdateRequest request, Member member);

  EventResponse.AdminEventDetailResponse getAdminEventDetail(Long eventId, Member member);
}

