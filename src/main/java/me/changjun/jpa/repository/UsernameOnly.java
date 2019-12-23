package me.changjun.jpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    @Value("#{target.username +' and '+target.age}")
    String getUsername();
}
