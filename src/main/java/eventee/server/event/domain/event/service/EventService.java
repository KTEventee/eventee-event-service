package eventee.server.event.domain.event.service;

import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventRequest.UpdateRequest;
import eventee.server.event.domain.event.dto.EventResponse;

import java.util.List;

public interface EventService {

  EventResponse.CreateResponse createEvent(Long memberId, EventRequest.CreateRequest request);

  EventResponse.JoinResponse joinEvent(Long memberId, EventRequest.JoinRequest inviteCode);
  EventResponse.EventWithGroupsResponse getEventGroups(Long memberId, Long eventId);
//  EventResponse.GroupPostsResponse getGroupPosts(MemberListDto.MemberDto member, Long eventId, Long groupId);

  EventResponse.InviteCodeValidateResponse validateInviteCode(String code);

  EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request);

  List<Long> getMembersByEvent(long eventId);
  void kickMember(EventRequest.KickMemberRequest request, Long memberId);

  EventResponse.UpdateEventResponse updateEventInfo(UpdateRequest request, Long memberId);

  EventResponse.AdminEventDetailResponse getAdminEventDetail(Long eventId, Long memberId);
}

