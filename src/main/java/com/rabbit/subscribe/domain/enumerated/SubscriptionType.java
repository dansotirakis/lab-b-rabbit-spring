package com.rabbit.subscribe.domain.enumerated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SubscriptionType {
    SUBSCRIPTION_PURCHASED("Ativa", "SUBSCRIPTION_PURCHASED"),
    SUBSCRIPTION_CANCELED("Cancelada", "SUBSCRIPTION_CANCELED"),
    SUBSCRIPTION_RESTARTED("Renovada", "SUBSCRIPTION_RESTARTED");

    String type;
    String code;

    public static SubscriptionType selectStatusReceived(String subType) {
        switch (subType) {
            case "SUBSCRIPTION_CANCELED":
                return SubscriptionType.SUBSCRIPTION_CANCELED;
            case "SUBSCRIPTION_PURCHASED":
                return SubscriptionType.SUBSCRIPTION_PURCHASED;
            case "SUBSCRIPTION_RESTARTED":
                return SubscriptionType.SUBSCRIPTION_RESTARTED;
            default:
                throw new IllegalStateException("Unexpected value: " + subType);
        }
    }
}
