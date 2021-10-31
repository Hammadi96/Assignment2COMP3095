package ca.gb.comp3095.foodrecipe.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
@ToString
public class CommonEntity {

    @Column(updatable = false)
    @Type(type = "org.hibernate.type.InstantType")
    @CreationTimestamp
    protected Instant creationTime;

    @Column(updatable = false)
    @Type(type = "org.hibernate.type.InstantType")
    @UpdateTimestamp
    protected Instant modificationTime;
}
