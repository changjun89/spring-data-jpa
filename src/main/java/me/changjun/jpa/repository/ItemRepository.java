package me.changjun.jpa.repository;

import me.changjun.jpa.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
