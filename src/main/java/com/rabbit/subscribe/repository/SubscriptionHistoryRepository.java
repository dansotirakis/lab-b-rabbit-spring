package com.rabbit.subscribe.repository;

import com.rabbit.subscribe.domain.SubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistory, Integer> {

}
