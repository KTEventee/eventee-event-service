package eventee.server.event.domain.group.service;

import eventee.server.event.domain.event.exception.EventErrorStatus;
import eventee.server.event.domain.event.exception.EventHandler;
import eventee.server.event.domain.event.model.Event;
import eventee.server.event.domain.event.repository.EventRepository;
import eventee.server.event.domain.group.dto.*;
import eventee.server.event.domain.group.model.Group;
import eventee.server.event.domain.group.model.MemberGroup;
import eventee.server.event.domain.group.repository.GroupRepository;
import eventee.server.event.domain.group.repository.MemberGroupRepository;
import eventee.server.event.domain.infrastructure.client.member.MemberListDto;
import eventee.server.event.global.exception.BaseException;
import eventee.server.event.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class GroupServiceImpl implements GroupService{

    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final MemberGroupRepository memberGroupRepository;

    @Transactional
    public void createAdditionalGroup(GroupReqeust.GroupCreateDto request, MemberListDto.MemberDto member) {

        // 1) eventId로 이벤트 찾기
        Event event = eventRepository.findByIdAndIsDeletedFalse(request.eventId())
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

        // 2) 그룹 번호(nextNo) 계산
        int nextNo = event.getGroups().size() + 1;


        // 3) Group 객체 생성
        Group group = Group.builder()
            .event(event)
            .groupName(request.groupName())
            .groupDescription(request.groupDescription())
            .groupImg(null)
            .groupNo(nextNo)
            .build();

        // 4) 저장
        Group saved = groupRepository.save(group);

        // 5) 생성자를 해당 그룹의 멤버로 배정
        MemberGroup memberGroup = MemberGroup.builder()
            .memberId(member.id())
            .group(saved)
            .build();

        memberGroupRepository.save(memberGroup);

        log.info("Group successfully created with eventId = {}", event.getId());
    }


    @Transactional
    public void updateGroup(GroupReqeust.GroupUpdateDto request){
        Group group = loadGroupById(request.groupId());
        if(group.updateGroup(request)) groupRepository.save(group);
    }

    @Transactional
    public void deleteGroup(Long id){
        Group group = loadGroupById(id);
        groupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public GroupResponse.ListDto getGroupByEvent(Long eventId,MemberListDto.MemberDto member){

        Event event = eventRepository.findByIdAndIsDeletedFalse(eventId).orElseThrow(
                () -> new BaseException(ErrorCode.EVENT_NOT_FOUND)
        );

        List<Group> groups = event.getGroups();

        Group myGroup = null;
        List<Group> otherGroups = new ArrayList<>();

        for(Group g : groups){
            if(isJoin(g,member)) myGroup = g;
            else otherGroups.add(g);
            otherGroups.add(g);
        }

        return GroupResponse.ListDto.from(myGroup, otherGroups);
    }

    private Boolean isJoin(Group g, MemberListDto.MemberDto member){
        List<MemberGroup> memberGroups = memberGroupRepository.findMemberGroupsByGroup(g);
        for(MemberGroup mg : memberGroups){
            if(mg.getMemberId().equals(member.id())) return true;
        }
        return false;
    }


    private Group loadGroupById(Long groupUd){
        return groupRepository.findGroupByGroupId(groupUd).orElseThrow(
                () -> new BaseException(ErrorCode.GROUP_NOT_FOUND)
        );
    }
}
