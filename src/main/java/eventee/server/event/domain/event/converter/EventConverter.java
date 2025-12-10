package eventee.server.event.domain.event.converter;


import eventee.server.event.domain.event.dto.EventResponse;
import eventee.server.event.domain.infrastructure.client.member.MemberListDto;
import eventee.server.event.domain.event.model.Event;
import eventee.server.event.domain.event.model.MemberEvent;
import eventee.server.event.domain.event.repository.MemberEventRepository;
import eventee.server.event.domain.group.model.Group;
import eventee.server.event.domain.infrastructure.client.post.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventConverter {
    private final MemberEventRepository memberEventRepository;

    public Event toEvent(String inviteCode, String title, String description,
                         String password, LocalDateTime startAt, LocalDateTime endAt, Integer teamCount) {

        return Event.builder()
                .title(title)
                .description(description)
                .password(password)
                .startAt(startAt)
                .endAt(endAt)
                .inviteCode(inviteCode)
                .teamCount(teamCount)
                .status("OPEN")
                .thumbnailUrl(null)
                .build();
    }

    public MemberEvent toHostRelation(MemberListDto.MemberDto member, Event event) {
        return MemberEvent.builder()
                .memberId(member.id())
                .event(event)
                .role(MemberEvent.MemberEventRole.HOST)
                .nickname(event.getTitle() + "관리자")
                .build();
    }

    public Group toGroup(int groupNo, MemberListDto.MemberDto leader, Event event) {
        return Group.builder()
                .groupName("팀 " + groupNo + "조")
                .groupDescription("팀 이름과 소개를 작성해주세요!")
                .groupImg(null)
                .groupNo(groupNo)
                .groupLeader(leader.nickname())
                .event(event)
                .build();
    }

    public EventResponse.CreateResponse toCreateResponse(Event event, MemberListDto.MemberDto member) {
        String inviteUrl = "https://www.eventee.cloud/invite/" + event.getInviteCode();

        return EventResponse.CreateResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .inviteCode(event.getInviteCode())
                .inviteUrl(inviteUrl)
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .createdAt(event.getCreatedAt())
                .creator(
                        EventResponse.CreateResponse.CreatorInfo.builder()
                                .memberId(member.id())
                                .nickname(member.nickname())
                                .profileImageUrl(member.profileImageUrl())
                                .build()
                )
                .build();
    }

    public EventResponse.EventWithGroupsResponse toEventWithGroupsResponse(Event event, List<Group> groups, MemberEvent.MemberEventRole role, String nickname) {

        List<EventResponse.EventWithGroupsResponse.GroupSummary> groupDtos =
                groups.stream()
                        .map(g -> EventResponse.EventWithGroupsResponse.GroupSummary.builder()
                                .groupId(g.getGroupId())
                                .groupName(g.getGroupName())
                                .groupDescription(g.getGroupDescription())
                                .groupImg(g.getGroupImg())
                                .groupNo(g.getGroupNo())
                                .groupLeader(g.getGroupLeader())
                                .build())
                        .toList();

        return EventResponse.EventWithGroupsResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventDescription(event.getDescription())
                .eventRole(role.name())
                .nickname(nickname)
                .thumbnailUrl(event.getThumbnailUrl())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .teamCount(event.getTeamCount())
                .groups(groupDtos)
                .build();
    }

//    public EventResponse.GroupPostsResponse toGroupPostsResponse(
//            Group group,
//            List<Post.PostDto> posts,
//            MemberListDto.MemberDto member
//    ) {
//
//        Event event = group.getEvent();
//
//        List<EventResponse.GroupPostsResponse.PostInfo> postInfos =
//                posts.stream()
//                        .map(post -> convertPostToDto(post, member, event))
//                        .toList();
//
//        return EventResponse.GroupPostsResponse.builder()
//                .groupId(group.getGroupId())
//                .groupName(group.getGroupName())
//                .posts(postInfos)
//                .build();
//    }


//    private EventResponse.GroupPostsResponse.PostInfo convertPostToDto(
//            Post.PostDto post,
//            MemberListDto.MemberDto currentUser,
//            Event event
//    ) {
//
//        boolean isMinePost = post.writerId().equals(currentUser.id());
//
//        String author = post.writerName();
//
//        MemberEvent me = memberEventRepository
//                .findByMemberIdAndEventAndIsDeletedFalse(post.writerId(), event)
//                .orElse(null);
//
//        String writerNickname = me != null ? me.getNickname() : null;
//        String writerProfileUrl = post.getMember().getProfileImageUrl();
//
//        List<EventResponse.GroupPostsResponse.CommentInfo> comments =
//                convertComments(post.getComments(), currentUser);
//
//        String pollQuestion = null;
//        List<EventResponse.GroupPostsResponse.VoteOptionInfo> pollOptions = null;
//        Integer userVote = null;
//
//        if (post.getPostType() == PostType.VOTE) {
//
//            pollQuestion = post.getVoteTitle();
//
//            String[] options = post.getVoteContent() != null
//                    ? post.getVoteContent().split("_")
//                    : new String[0];
//
//            List<VoteLog> logs = post.getVoteLogs();
//            int totalVotes = logs.size();
//
//            pollOptions = new ArrayList<>();
//
//            for (int i = 0; i < options.length; i++) {
//                int optionNo = i + 1;
//                String text = options[i];
//
//                int votes = (int) logs.stream()
//                        .filter(v -> v.getVoteNum() == optionNo)
//                        .count();
//
//                int percent = totalVotes > 0 ? (votes * 100 / totalVotes) : 0;
//
//                boolean isMine = logs.stream()
//                        .anyMatch(v -> v.getMember().getId().equals(currentUser.getId())
//                                && v.getVoteNum() == optionNo);
//
//                pollOptions.add(
//                        EventResponse.GroupPostsResponse.VoteOptionInfo.builder()
//                                .optionNo(optionNo)
//                                .text(text)
//                                .votes(votes)
//                                .percent(percent)
//                                .isMine(isMine)
//                                .build()
//                );
//            }
//
//            userVote = logs.stream()
//                    .filter(v -> v.getMember().getId().equals(currentUser.getId()))
//                    .map(VoteLog::getVoteNum)
//                    .findFirst()
//                    .orElse(null);
//        }
//
//        return EventResponse.GroupPostsResponse.PostInfo.builder()
//                .postId(post.getPostId())
//                .author(author)
//                .writerNickname(writerNickname)
//                .writerProfileUrl(writerProfileUrl)
//                .content(post.getContent())
//                .type(post.getPostType().type.toLowerCase())
//                .createdAt(post.getCreatedAt())
//                .comments(comments)
//                .pollQuestion(pollQuestion)
//                .pollOptions(pollOptions)
//                .userVote(userVote)
//                .isMine(isMinePost)
//                .build();
//    }
//
//
//    private List<EventResponse.GroupPostsResponse.CommentInfo> convertComments(
//            List<Comment> comments,
//            MemberListDto.MemberDto currentUser
//    ) {
//        return comments.stream()
//                .map(c -> EventResponse.GroupPostsResponse.CommentInfo.builder()
//                        .commentId(c.getCommentId())
//                        .content(c.getContent())
//                        .writerNickname(c.getMember().getNickname())
//                        .writerProfileUrl(c.getMember().getProfileImageUrl())
//                        .createdAt(c.getCreatedAt())
//                        .isMine(c.getMember().getId().equals(currentUser.id()))
//                        .build())
//                .toList();
//    }

    public EventResponse.JoinResponse toJoinResponse(Event event, MemberEvent memberEvent) {

        List<EventResponse.JoinResponse.GroupInfo> groupInfos =
                event.getGroups().stream()
                        .map(group -> EventResponse.JoinResponse.GroupInfo.builder()
                                .groupId(group.getGroupId())
                                .groupName(group.getGroupName())
                                .groupDescription(group.getGroupDescription())
                                .groupImg(group.getGroupImg())
                                .groupNo(group.getGroupNo())
                                .groupLeader(group.getGroupLeader())
                                .build())
                        .toList();

        return EventResponse.JoinResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .thumbnailUrl(event.getThumbnailUrl())
                .teamCount(event.getTeamCount())
                .role(memberEvent.getRole().name())
                .nickname(memberEvent.getNickname())
                .groups(groupInfos)
                .build();
    }
}
