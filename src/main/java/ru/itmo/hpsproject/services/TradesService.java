package ru.itmo.hpsproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmo.hpsproject.model.entity.TradeEntity;
import ru.itmo.hpsproject.repositories.TradesRepository;

import java.util.List;

@Service
public class TradesService {
    private final TradesRepository tradesRepository;

    @Autowired
    public TradesService(TradesRepository tradesRepository) {
        this.tradesRepository = tradesRepository;
    }

    public List<TradeEntity> findUserReceivingTrades(Long userId) {
        return tradesRepository.findByAccepterId(userId);
    }

    public List<TradeEntity> findUserSendingTrades(Long userId) {
        return tradesRepository.findByOffererId(userId);
    }
}
