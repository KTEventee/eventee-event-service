package eventee.server.event.domain.group.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member_group")
@SQLDelete(sql = "UPDATE member_group SET is_deleted = true, deleted_at = now() where group_id = ?")
@SQLRestriction("is_deleted is FALSE")
public class MemberGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberGroupId;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public MemberGroup(Long memberId, Group group){
        this.memberId = memberId;
        this.group = group;
    }
}
