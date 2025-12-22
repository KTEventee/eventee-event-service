package eventee.server.event.domain.event.repository;

import eventee.server.event.domain.event.model.Event;
import eventee.server.event.domain.event.model.MemberEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberEventRepository extends JpaRepository<MemberEvent, Long> {

  List<MemberEvent> findAllByMemberIdAndIsDeletedFalse(Long memberId);

  boolean existsByMemberIdAndEventAndIsDeletedFalse(Long memberId, Event event);

  Optional<MemberEvent> findByMemberIdAndEventAndIsDeletedFalse(Long memberId, Event event);
  List<MemberEvent> findMemberEventsByEventAndIsDeletedFalse(Event event);

  @Query("SELECT COUNT(me) FROM MemberEvent me WHERE me.event.id = :eventId AND me.isDeleted = false")
  Long countByEventId(@Param("eventId") Long eventId);
}
