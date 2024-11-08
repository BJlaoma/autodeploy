package com.autodeploy.demo.repository;

import com.autodeploy.demo.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServerRepository extends JpaRepository<Server, String> {
    List<Server> findByUseruuid(String useruuid);
} 