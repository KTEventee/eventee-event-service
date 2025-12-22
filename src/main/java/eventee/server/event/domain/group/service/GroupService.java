package eventee.server.event.domain.group.service;

import eventee.server.event.domain.group.dto.*;

public interface GroupService {
    void createAdditionalGroup(GroupReqeust.GroupCreateDto request, Long memberId);
    void deleteGroup(Long id);
    void updateGroup(GroupReqeust.GroupUpdateDto request);
    GroupResponse.ListDto getGroupByEvent(Long eventId,Long memberId);
}
