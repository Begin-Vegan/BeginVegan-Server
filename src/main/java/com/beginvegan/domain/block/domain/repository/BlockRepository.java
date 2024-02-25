package com.beginvegan.domain.block.domain.repository;

import com.beginvegan.domain.block.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {
}
