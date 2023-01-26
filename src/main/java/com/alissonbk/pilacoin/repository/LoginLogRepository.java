package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> { }
