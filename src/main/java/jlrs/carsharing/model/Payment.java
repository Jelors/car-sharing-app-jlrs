package jlrs.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "payment")
@SQLDelete(sql = "UPDATE payment SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rental_id", nullable = false)
    private Long rentalId;

    @Column(name = "session_url", nullable = false ,length = 2048)
    private URL sessionUrl; // URL for the payment session with a payment provider

    @Column(name = "session_id", nullable = false)
    private Long sessionId; // ID of the payment session

    @Column(nullable = false)
    private BigDecimal total; // calculated rental total price ($USD)

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public enum Status {
        PENDING,
        PAID
    }

    public enum Type {
        PAYMENT,
        FINE
    }
}
