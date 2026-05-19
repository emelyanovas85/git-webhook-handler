package ru.cbr.bugbusters.gitwebhookhandler.review.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "review_event",
        uniqueConstraints = @UniqueConstraint(name = "uq_review_event", columnNames = {"project_id", "mr_iid", "note_id"})
)
public class ReviewEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "mr_iid", nullable = false)
    private Long mrIid;

    @Column(name = "note_id", nullable = false)
    private Long noteId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
