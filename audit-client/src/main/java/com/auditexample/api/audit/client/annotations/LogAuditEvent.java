package com.auditexample.api.audit.client.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Component
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LogAuditEvent {

    int customerId() default 0;
    String groupId() default "";
    int employeeId() default 0;
    String objectId() default "";
    String objectDetails() default "";
    String parentRefId() default "";
    String propertyName() default "";
    String propertyValue() default "";
    String eventType();
    String eventDetails() default "";
}

