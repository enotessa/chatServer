package com.enotessa.repositories;

import com.enotessa.entities.Message;
import com.enotessa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByUserOrderByIdAsc(User user);
}
