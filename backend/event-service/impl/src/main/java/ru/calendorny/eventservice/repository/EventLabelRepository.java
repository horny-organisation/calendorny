package ru.calendorny.eventservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.calendorny.eventservice.data.entity.EventLabelEntity;

public interface EventLabelRepository extends JpaRepository<EventLabelEntity, Long> {

}
