package ru.calendorny.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.data.entity.EventLabelEntity;

public interface EventLabelRepository extends JpaRepository<EventLabelEntity, Long> {
}
