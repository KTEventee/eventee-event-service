package eventee.server.event.domain.group.repository;

import eventee.server.event.domain.group.model.Group;
import eventee.server.event.domain.group.model.MemberGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberGroupRepository extends JpaRepository<MemberGroup,Long> {
    List<MemberGroup> findMemberGroupsByGroup(Group group);
}
