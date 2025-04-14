package com.alexartauddev.licenseforge.domain.license.entity;

import jakarta.persistence.*;
import jakarta.persistence.FetchType;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "licenses")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String licenseKey;

    @Column(nullable = false)
    private UUID appId;

    @Column(nullable = false)
    private String customerId;

    @Column
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private int maxActivations;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "license_activations", joinColumns = @JoinColumn(name = "license_id"))
    @Column(name = "hardware_id")
    @Builder.Default
    private Set<String> hardwareIds = new HashSet<>();

    public boolean isExpired() {
        // Check if expires at is null first
        if (expiresAt == null) return false; // No expiration date means never expires
        // Get current time in UTC to match database time
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expiresAt);
    }

    public boolean canActivate(String hardwareId) {
        return !isExpired() && !revoked && (hardwareIds.contains(hardwareId) || hardwareIds.size() < maxActivations);
    }

    public boolean activate(String hardwareId) {
        if (!canActivate(hardwareId)) return false;
        return hardwareIds.add(hardwareId);
    }

    public boolean deactivate(String hardwareId) {
        return hardwareIds.remove(hardwareId);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        License license = (License) o;
        return getId() != null && Objects.equals(getId(), license.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
