package eventee.server.event.domain.infrastructure.client.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class Post {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VoteOptionDto(
            int optionNo,
            String text,
            int votes,
            int percent,
            boolean isMine
    ) { }

    // ==== 댓글 DTO (필요한 필드만) ====
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CommentDto(
            Long commentId,
            String content,
            String writerName
    ) { }

    // ==== 게시글 DTO ====
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PostDto(
            long postId,
            String content,
            String writerName,
            Long writerId,
            String type,
            String voteTitle,
            List<VoteOptionDto> voteOptions,
            List<CommentDto> comments,
            boolean isWrite
    ) { }

    // ==== 그룹별 게시글 리스트 DTO ====
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PostListDto(
            int groupNum,
            List<PostDto> posts
    ) { }

    // ==== 이벤트 내 전체 그룹 게시글 리스트 DTO (루트) ====
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PostListByGroupDto(
            List<PostListDto> lists
    ) { }

}
