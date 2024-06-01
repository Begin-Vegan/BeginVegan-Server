package com.beginvegan.domain.alarm.domain.repository;

import com.beginvegan.domain.alarm.domain.Alarm;
import com.beginvegan.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUser(User user);
}
