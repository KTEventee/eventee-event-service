package eventee.server.event.domain.event.controller;

import eventee.server.common.jwt.exception.JwtErrorCode;
import eventee.server.common.jwt.exception.JwtHandler;
import eventee.server.event.domain.event.dto.EventRequest;
import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.event.service.EventService;
import eventee.server.common.exception.BaseResponse;
import eventee.server.common.exception.codes.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
    public BaseResponse<EventResponse.CreateResponse> createEvent(HttpServletRequest request,
            @Valid @RequestBody EventRequest.CreateRequest requestDto, Authentication authentication
            ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        EventResponse.CreateResponse response = eventService.createEvent(memberId, requestDto);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 입장")
    @PostMapping("/join")
    public BaseResponse<EventResponse.JoinResponse> joinEvent(HttpServletRequest request,
            @Valid @RequestBody EventRequest.JoinRequest requestDto,
                                                              Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        EventResponse.JoinResponse response = eventService.joinEvent(memberId, requestDto);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 그룹 목록 조회")
    @GetMapping("/{eventId}/groups")
    public BaseResponse<EventResponse.EventWithGroupsResponse> getEventGroups(HttpServletRequest request,
            @PathVariable Long eventId,Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
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
            @RequestParam String code,Authentication authentication
    ) {
        EventResponse.InviteCodeValidateResponse response = eventService.validateInviteCode(code);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "비밀번호 검증")
    @PostMapping("/verify")
    public BaseResponse<EventResponse.EventPasswordVerifyResponse> verifyEventPassword(
            @Valid @RequestBody EventRequest.PasswordVerifyRequest request,Authentication authentication
    ) {
        EventResponse.EventPasswordVerifyResponse response = eventService.verifyEventPassword(request);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "이벤트 멤버 가져오기")
    @GetMapping("/admin/members")
    public BaseResponse<List<Long>> getMembers(HttpServletRequest request,
            @RequestParam Long eventId,Authentication authentication){
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        List<Long> response = eventService.getMembersByEvent(eventId);
        return BaseResponse.onSuccess(response);
    }

    @Operation(summary = "사용자 강퇴")
    @PostMapping("/admin/ban")
    public BaseResponse<String> kickMember(HttpServletRequest request,Authentication authentication,
            @RequestBody EventRequest.KickMemberRequest requestDto){
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        eventService.kickMember(requestDto, memberId);
        return BaseResponse.onSuccess("success");
    }

    @Operation(summary = "관리자용 이벤트 상세 조회")
    @GetMapping("/admin/detail")
    public BaseResponse<EventResponse.AdminEventDetailResponse> getAdminEventDetail(
        HttpServletRequest request,
            @RequestParam Long eventId,
        Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        EventResponse.AdminEventDetailResponse response = eventService.getAdminEventDetail(eventId, memberId);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "관리자용 이벤트 정보 수정")
    @PatchMapping("/admin")
    public BaseResponse<EventResponse.UpdateEventResponse> updateEventInfo(
        HttpServletRequest request,
        Authentication authentication,
            @Valid @RequestBody EventRequest.UpdateRequest requestDto
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        EventResponse.UpdateEventResponse response = eventService.updateEventInfo(requestDto, memberId);
        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }

    @Operation(summary = "내가 참여한 이벤트 목록 조회 (마이페이지)")
    @GetMapping("/me")
    public BaseResponse<List<EventResponse.JoinedEventResponse>> getMyEvents(
        HttpServletRequest request,
        Authentication authentication
    ) {
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }

        List<EventResponse.JoinedEventResponse> response =
            eventService.getMyJoinedEvents(memberId);

        return BaseResponse.of(SuccessCode.SUCCESS, response);
    }


}

