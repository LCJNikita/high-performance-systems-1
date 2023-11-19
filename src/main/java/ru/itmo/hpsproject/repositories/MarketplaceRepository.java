package ru.itmo.hpsproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.hpsproject.model.entity.MarketplaceItemEntity;

public interface MarketplaceRepository extends JpaRepository<MarketplaceItemEntity, Long> {

    // посмотреть лоты на маркете все (с пагинацией)
    // лоты с каким-нибудь фильтром
    // лоты по возрастанию, убыванию цены
}
