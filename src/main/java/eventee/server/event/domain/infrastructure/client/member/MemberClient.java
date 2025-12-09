//package eventee.server.event.domain.infrastructure.client.member;
//
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(name = "member-service", url = "${member-service.url}")
//public interface MemberClient {
//    @GetMapping("/internal/members/{id}")
//    MemberListDto.MemberDto getMember(@PathVariable Long id);
//}