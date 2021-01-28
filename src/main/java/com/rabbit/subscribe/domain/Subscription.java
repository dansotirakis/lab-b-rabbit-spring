package com.rabbit.subscribe.domain;

import com.rabbit.subscribe.domain.enumerated.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "subscription")
public class Subscription {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    @Enumerated
    private SubscriptionType status;
}
