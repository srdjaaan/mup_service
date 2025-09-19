package mup.repository;

import mup.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserJmbgOrderByCreatedAtDesc(String userJmbg);

    List<Notification> findByUserJmbgAndIsReadFalseOrderByCreatedAtDesc(String userJmbg);

    long countByUserJmbgAndIsReadFalse(String userJmbg);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userJmbg = :userJmbg")
    void markAllAsReadByUserJmbg(@Param("userJmbg") String userJmbg);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notificationId")
    void markAsReadById(@Param("notificationId") Long notificationId);
}