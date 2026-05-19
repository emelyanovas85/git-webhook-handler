package ru.cbr.bugbusters.gitwebhookhandler.review.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewEventRepository extends JpaRepository<ReviewEventEntity, Long> {
    boolean existsByProjectIdAndMrIidAndNoteId(Long projectId, Long mrIid, Long noteId);
}
