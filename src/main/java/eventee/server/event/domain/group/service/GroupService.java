package eventee.server.event.domain.group.service;

import eventee.server.event.domain.group.dto.*;
import eventee.server.event.domain.infrastructure.client.member.MemberListDto;

public interface GroupService {
    void createAdditionalGroup(GroupReqeust.GroupCreateDto request, MemberListDto.MemberDto member);
    void deleteGroup(Long id);
    void updateGroup(GroupReqeust.GroupUpdateDto request);
    GroupResponse.ListDto getGroupByEvent(Long eventId,MemberListDto.MemberDto member);
//    void updateLeader(GroupReqeust.GroupUpdateLeaderDto request);
//    void enterGroup(Long id, MemberListDto.MemberDto member);
//    void leaveGroup(Long id,MemberListDto.MemberDto member);
//    void moveGroup(GroupReqeust.GroupMoveDto request,MemberListDto.MemberDto member);
}
