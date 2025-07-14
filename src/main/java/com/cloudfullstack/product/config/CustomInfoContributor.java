package com.cloudfullstack.product.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("service", "product-service")
            .withDetail("version", "1.0.0")
            .withDetail("description", "Modern product management microservice");
    }
}