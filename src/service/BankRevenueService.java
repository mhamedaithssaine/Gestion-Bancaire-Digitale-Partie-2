package service;

import model.BankRevenue;
import repository.impl.BankRevenueRepositoryImp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BankRevenueService {
    private static BankRevenueService instance ;
    private final BankRevenueRepositoryImp revenueRepository;


    private BankRevenueService() {
        this.revenueRepository = BankRevenueRepositoryImp.getInstance();
    }

    public static BankRevenueService getInstance() {
        if (instance == null) {
            instance = new BankRevenueService();
        }
        return instance;
    }

    public boolean recordRevenue(String sourceType, String sourceSubType, BigDecimal amount, String currency, UUID trasactionId, String note) {
        BankRevenue revenue = new BankRevenue();
        revenue.setId(UUID.randomUUID());
        revenue.setSourceType(sourceType);
        revenue.setSourceSubType(sourceSubType);
        revenue.setAmount(amount);
        revenue.setCurrency(currency);
        revenue.setOccurredAt(LocalDateTime.now());
        revenue.setTransactionId(trasactionId);
        revenue.setNote(note);

        return revenueRepository.save(revenue);
    }

    public Optional<BankRevenue> getRevenueById(UUID id) {
        return revenueRepository.findById(id);
    }

    public List<BankRevenue> getAllRevenues() {
        return revenueRepository.findAll();
    }

    public List<BankRevenue> getRevenuesByTransfer(UUID transferId) {
        return revenueRepository.findByTransferId(transferId);
    }
}
