package eventee.server.event.domain.event.service;

import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventRequest.UpdateRequest;
import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.infrastructure.client.member.MemberListDto;

import java.util.List;

public interface EventService {

  EventResponse.CreateResponse createEvent(MemberListDto.MemberDto member, EventRequest.CreateRequest request);

  EventResponse.JoinResponse joinEvent(MemberListDto.MemberDto member, EventRequest.JoinRequest inviteCode);
  EventResponse.EventWithGroupsResponse getEventGroups(MemberListDto.MemberDto member, Long eventId);
//  EventResponse.GroupPostsResponse getGroupPosts(MemberListDto.MemberDto member, Long eventId, Long groupId);

  EventResponse.InviteCodeValidateResponse validateInviteCode(String code);

  EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request);

  List<MemberListDto.MemberDto> getMembersByEvent(long eventId);
  void kickMember(EventRequest.KickMemberRequest request, MemberListDto.MemberDto member);

  EventResponse.UpdateEventResponse updateEventInfo(UpdateRequest request, MemberListDto.MemberDto member);

  EventResponse.AdminEventDetailResponse getAdminEventDetail(Long eventId, MemberListDto.MemberDto member);
}

