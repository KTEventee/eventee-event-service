package eventee.server.event.domain.event.service;

import eventee.server.event.domain.event.converter.EventConverter;
import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.event.dto.EventResponse.AdminEventDetailResponse;
import eventee.server.event.domain.event.dto.EventResponse.JoinedEventResponse;
import eventee.server.event.domain.event.dto.EventResponse.UpdateEventResponse;
import eventee.server.event.domain.group.repository.GroupRepository;
import eventee.server.event.domain.event.exception.EventErrorStatus;
import eventee.server.event.domain.event.exception.EventHandler;
import eventee.server.event.domain.event.model.Event;
import eventee.server.event.domain.event.model.MemberEvent;
import eventee.server.event.domain.event.model.MemberEvent.MemberEventRole;
import eventee.server.event.domain.event.repository.EventRepository;
import eventee.server.event.domain.event.repository.MemberEventRepository;
import eventee.server.event.domain.group.model.Group;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

  private final EventRepository eventRepository;
  private final MemberEventRepository memberEventRepository;
  private final GroupRepository groupRepository;

  private final EventConverter eventConverter;

  /* ============================================
      1. 이벤트 생성
  ============================================ */
  @Transactional
  @Override
  public EventResponse.CreateResponse createEvent(Long memberId, EventRequest.CreateRequest request) {

    if (request.teamCount() == null || request.teamCount() <= 0) {
      throw new EventHandler(EventErrorStatus.EVENT_TEAM_COUNT_INVALID);
    }

    String inviteCode = RandomStringUtils.randomAlphabetic(6).toUpperCase();

    Event event = eventConverter.toEvent(
        inviteCode,
        request.title(),
        request.description(),
        request.password(),
        request.startAt(),
        request.endAt(),
        request.teamCount()
    );

    event = eventRepository.save(event);

    // HOST 연결
    MemberEvent hostRelation = eventConverter.toHostRelation(memberId, event);
    memberEventRepository.save(hostRelation);

    // 팀 자동 생성
    for (int i = 1; i <= request.teamCount(); i++) {
      Group group = eventConverter.toGroup(i, event);
      groupRepository.save(group);
    }

    return eventConverter.toCreateResponse(event, memberId);
  }

  /* ============================================
      2. 이벤트 입장
  ============================================ */
  @Transactional
  @Override
  public EventResponse.JoinResponse joinEvent(Long memberId, EventRequest.JoinRequest request) {

    Event event = eventRepository.findByInviteCode(request.inviteCode())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    if (!Objects.equals(event.getPassword(), request.password())) {
      throw new EventHandler(EventErrorStatus.EVENT_PASSWORD_INVALID);
    }

    MemberEvent memberEvent = memberEventRepository
        .findByMemberIdAndEventAndIsDeletedFalse(memberId, event)
        .orElse(null);

    if (memberEvent == null) {
      memberEvent = MemberEvent.builder()
          .memberId(memberId)
          .event(event)
          .role(MemberEventRole.PARTICIPANT)
          .nickname(request.nickname())
          .build();
      memberEventRepository.save(memberEvent);
    } else {
      memberEvent.updateNickname(request.nickname());
    }

    return eventConverter.toJoinResponse(event, memberEvent);
  }

  /* ============================================
      3. 이벤트 + 그룹 목록 조회
  ============================================ */
  @Transactional(readOnly = true)
  @Override
  public EventResponse.EventWithGroupsResponse getEventGroups(Long memberId, Long eventId) {

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    MemberEvent relation = memberEventRepository
        .findByMemberIdAndEventAndIsDeletedFalse(memberId, event)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED));

    List<Group> groups = groupRepository.findAllByEventId(eventId);

    return eventConverter.toEventWithGroupsResponse(event, groups, relation.getRole(), relation.getNickname());
  }

//  /* ============================================
//      4. 그룹별 포스트 조회
//  ============================================ */
//  @Transactional(readOnly = true)
//  @Override
//  public EventResponse.GroupPostsResponse getGroupPosts(MemberListDto.MemberDto member, Long eventId, Long groupId) {
//
//    Event event = eventRepository.findById(eventId)
//        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));
//
//    boolean isParticipant =
//        memberEventRepository.existsByMemberIdAndEventAndIsDeletedFalse(member.id(), event);
//
//    if (!isParticipant) {
//      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
//    }
//
//    Group group = groupRepository.findGroupByGroupId(groupId)
//        .orElseThrow(() -> new EventHandler(EventErrorStatus.GROUP_NOT_FOUND));
//
//    if (!Objects.equals(group.getEvent().getId(), eventId)) {
//      throw new EventHandler(EventErrorStatus.GROUP_NOT_BELONGS_TO_EVENT);
//    }
//
//    //fixme post 처리하기
////    List<Post> posts = postRepository.findAllByGroupAndIsDeletedFalse(group);
//
//    return eventConverter.toGroupPostsResponse(group, posts, member);
//  }

  /* ============================================
      5. 초대 코드 유효성 검증
  ============================================ */
  @Transactional(readOnly = true)
  @Override
  public EventResponse.InviteCodeValidateResponse validateInviteCode(String code) {

    Event event = eventRepository.findByInviteCode(code)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    return new EventResponse.InviteCodeValidateResponse(
        true,
        "초대 코드가 유효합니다.",
        event.getId()
    );
  }

  /* ============================================
      6. 비밀번호 검증
  ============================================ */
  @Transactional(readOnly = true)
  @Override
  public EventResponse.EventPasswordVerifyResponse verifyEventPassword(EventRequest.PasswordVerifyRequest request) {

    Event event = eventRepository.findByInviteCode(request.inviteCode())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    if (!Objects.equals(event.getPassword(), request.password())) {
      throw new EventHandler(EventErrorStatus.EVENT_PASSWORD_INVALID);
    }

    return EventResponse.EventPasswordVerifyResponse.builder()
        .valid(true)
        .eventId(event.getId())
        .title(event.getTitle())
        .message("비밀번호가 일치합니다.")
        .build();
  }

  /* ============================================
      7. 이벤트 참여자 목록
  ============================================ */
  @Transactional(readOnly = true)
  public List<Long> getMembersIdByEvent(long eventId) {

    Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    List<MemberEvent> relations = memberEventRepository
        .findMemberEventsByEventAndIsDeletedFalse(event);

    //fixme 호출한대로 그냥 출력하기


    return relations.stream()
        .map(MemberEvent::getMemberId)
        .toList();
  }

  public List<String> getMembersNamesByEvent(long eventId){
    Event event = eventRepository.findByIdAndIsDeletedFalse(eventId)
            .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    List<MemberEvent> relations = memberEventRepository
            .findMemberEventsByEventAndIsDeletedFalse(event);

    //fixme 호출한대로 그냥 출력하기


    return relations.stream()
            .map(MemberEvent::getNickname)
            .toList();
  }

  /* ============================================
      8. 강퇴
  ============================================ */
  @Transactional
  public void kickMember(EventRequest.KickMemberRequest request, Long memberId) {

    Event event = eventRepository.findByIdAndIsDeletedFalse(request.eventId())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    MemberEvent me = memberEventRepository.findByMemberIdAndEventAndIsDeletedFalse(memberId, event)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_MEMBER_NOT_FOUND));

    memberEventRepository.delete(me);
  }

  /* ============================================
      9. 이벤트 정보 수정 → 수정된 값 반환
  ============================================ */
  @Transactional
  @Override
  public UpdateEventResponse updateEventInfo(EventRequest.UpdateRequest request, Long adminId) {

    Event event = eventRepository.findById(request.eventId())
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    MemberEvent relation = memberEventRepository
        .findByMemberIdAndEventAndIsDeletedFalse(adminId, event)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED));

    if (relation.getRole() != MemberEventRole.HOST) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    // LocalDate → LocalDateTime 변환
    LocalDateTime startDateTime = request.startAt() != null ? request.startAt().atStartOfDay() : null;
    LocalDateTime endDateTime = request.endAt() != null ? request.endAt().atTime(23, 59, 59) : null;


    event.updateInfo(
        request.title(),
        request.description(),
        startDateTime,
        endDateTime
    );

    eventRepository.save(event);

    Long participantCount = memberEventRepository.countByEventId(event.getId());
    Long groupCount = groupRepository.countByEventId(event.getId());

    return UpdateEventResponse.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .description(event.getDescription())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .inviteCode(event.getInviteCode())
        .participantCount(participantCount)
        .groupCount(groupCount)
        .build();

  }

  /* ============================================
      10. 관리자 대시보드 상세 조회
  ============================================ */
  @Transactional(readOnly = true)
  public AdminEventDetailResponse getAdminEventDetail(Long eventId, Long adminId) {

    Event event = eventRepository.findById(eventId)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_NOT_FOUND));

    MemberEvent relation = memberEventRepository
        .findByMemberIdAndEventAndIsDeletedFalse(adminId, event)
        .orElseThrow(() -> new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED));

    if (relation.getRole() != MemberEventRole.HOST) {
      throw new EventHandler(EventErrorStatus.EVENT_ACCESS_DENIED);
    }

    Long participantCount = memberEventRepository.countByEventId(eventId);
    Long groupCount = groupRepository.countByEventId(eventId);

    return AdminEventDetailResponse.builder()
        .eventId(event.getId())
        .title(event.getTitle())
        .description(event.getDescription())
        .startAt(event.getStartAt())
        .endAt(event.getEndAt())
        .inviteCode(event.getInviteCode())
        .participantCount(participantCount)
        .groupCount(groupCount)
        .build();
  }

  @Override
  @Transactional(readOnly = true)
  public List<JoinedEventResponse> getMyJoinedEvents(Long memberId) {

    // 1. 내가 참여한 모든 이벤트 관계 조회
    List<MemberEvent> relations =
        memberEventRepository.findByMemberIdAndIsDeletedFalse(memberId);

    if (relations.isEmpty()) {
      return List.of();
    }

    // 2. 각 이벤트별 DTO 매핑
    return relations.stream().map(relation -> {
      Event event = relation.getEvent();

      // 참여자 수
      int participantsCount = memberEventRepository.countByEventId(event.getId()).intValue();


      // 참여자 프로필 이미지 (최대 3명)
      // → memberId만 반환 (프론트에서 이미지 URL 매핑 or 추후 BFF에서 처리)
      List<String> participantProfileImages =
          memberEventRepository
              .findMemberEventsByEventAndIsDeletedFalse(event)
              .stream()
              .limit(3)
              .map(me -> String.valueOf(me.getMemberId())) // placeholder
              .toList();

      return JoinedEventResponse.builder()
          .eventId(event.getId())
          .title(event.getTitle())
          .thumbnailUrl(event.getThumbnailUrl())
          .inviteCode(event.getInviteCode())
          .startAt(event.getStartAt())
          .endAt(event.getEndAt())
          .participantsCount(participantsCount)
          .participantProfileImages(participantProfileImages)
          .role(relation.getRole().name())
          .build();

    }).toList();
  }

}

