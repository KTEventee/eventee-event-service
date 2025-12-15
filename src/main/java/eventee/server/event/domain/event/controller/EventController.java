package eventee.server.event.domain.event.controller;

import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.event.service.EventService;
import eventee.server.common.exception.BaseResponse;
import eventee.server.common.exception.codes.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Event", description = "이벤트 생성 및 입장 관련 API")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventController {

    private final EventService eventService;


    @Operation(summary = "이벤트 생성")
    @PostMapping
    public BaseResponse<EventResponse.CreateResponse> createEvent(
            @Valid @RequestBody EventRequest.CreateRequest request
            ) {
        Long memberId = null;
        EventResponse.CreateResponse response = eventService.createEvent(memberId, request);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 입장")
    @PostMapping("/join")
    public BaseResponse<EventResponse.JoinResponse> joinEvent(
            @Valid @RequestBody EventRequest.JoinRequest request
    ) {
        Long memberId = null;
        EventResponse.JoinResponse response = eventService.joinEvent(memberId, request);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 그룹 목록 조회")
    @GetMapping("/{eventId}/groups")
    public BaseResponse<EventResponse.EventWithGroupsResponse> getEventGroups(
            @PathVariable Long eventId
    ) {
        Long memberId = null;
        EventResponse.EventWithGroupsResponse response = eventService.getEventGroups(memberId, eventId);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

//    @Operation(summary = "그룹별 포스트 조회")
//    @GetMapping("/{eventId}/groups/{groupId}/posts")
//    public BaseResponse<EventResponse.GroupPostsResponse> getGroupPosts(
//            @PathVariable Long eventId,
//            @PathVariable Long groupId
//    ) {
//        MemberListDto.MemberDto member = null;
//        EventResponse.GroupPostsResponse response = eventService.getGroupPosts(member, eventId, groupId);
//        logResponse(response);
//        return BaseResponse.of(SuccessCode.SUCCESS, response);
//    }

    @Operation(summary = "초대 코드 검증")
    @GetMapping("/validate")
    public BaseResponse<EventResponse.InviteCodeValidateResponse> validateInviteCode(
            @RequestParam String code
    ) {
        EventResponse.InviteCodeValidateResponse response = eventService.validateInviteCode(code);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "비밀번호 검증")
    @PostMapping("/verify")
    public BaseResponse<EventResponse.EventPasswordVerifyResponse> verifyEventPassword(
            @Valid @RequestBody EventRequest.PasswordVerifyRequest request
    ) {
        EventResponse.EventPasswordVerifyResponse response = eventService.verifyEventPassword(request);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 멤버 가져오기")
    @GetMapping("/admin/members")
    public BaseResponse<List<Long>> getMembers(
            @RequestParam Long eventId){
        Long memberId = null;
        List<Long> response = eventService.getMembersByEvent(eventId);
        return BaseResponse.onSuccess(response);
    }

    @Operation(summary = "사용자 강퇴")
    @PostMapping("/admin/ban")
    public BaseResponse<String> kickMember(
            @RequestBody EventRequest.KickMemberRequest request){
        Long memberId = null;
        eventService.kickMember(request, memberId);
        return BaseResponse.onSuccess("success");
    }

    @Operation(summary = "관리자용 이벤트 상세 조회")
    @GetMapping("/admin/detail")
    public BaseResponse<EventResponse.AdminEventDetailResponse> getAdminEventDetail(
            @RequestParam Long eventId
    ) {
        Long memberId = null;
        EventResponse.AdminEventDetailResponse response = eventService.getAdminEventDetail(eventId, memberId);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "관리자용 이벤트 정보 수정")
    @PatchMapping("/admin")
    public BaseResponse<EventResponse.UpdateEventResponse> updateEventInfo(
            @Valid @RequestBody EventRequest.UpdateRequest request
    ) {
        Long memberId = null;
        EventResponse.UpdateEventResponse response = eventService.updateEventInfo(request, memberId);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

}

