package com.rabbit.subscribe.repository;

import com.rabbit.subscribe.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

}
