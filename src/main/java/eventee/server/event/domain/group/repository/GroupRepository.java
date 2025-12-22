package eventee.server.event.domain.group.repository;


import eventee.server.event.domain.group.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Long> {
    Optional<Group> findGroupByGroupId(Long groupId);
    List<Group> findAllByEventId(Long eventId);

    List<Group> findByGroupIdIn(List<Long> groupIds);

    @Query("SELECT COUNT(g) FROM Group g WHERE g.event.id = :eventId AND g.isDeleted = false")
    Long countByEventId(@Param("eventId") Long eventId);

}
