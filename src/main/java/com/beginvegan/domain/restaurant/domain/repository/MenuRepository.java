package com.beginvegan.domain.restaurant.domain.repository;

import com.beginvegan.domain.restaurant.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
