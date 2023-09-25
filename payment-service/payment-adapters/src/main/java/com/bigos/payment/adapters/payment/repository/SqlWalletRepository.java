package com.bigos.payment.adapters.payment.repository;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.payment.application.payment.exception.WalletNotFoundException;
import com.bigos.payment.domain.core.Wallet;
import com.bigos.payment.domain.port.WalletRepository;
import com.bigos.payment.entities.WalletEntity;
import com.bigos.payment.entities.WalletEntityMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlWalletRepository implements WalletRepository {

    private final WalletRepositoryJpa walletRepositoryJpa;
    private final WalletEntityMapper walletEntityMapper;

    public SqlWalletRepository(WalletRepositoryJpa walletRepositoryJpa, WalletEntityMapper walletEntityMapper) {
        this.walletRepositoryJpa = walletRepositoryJpa;
        this.walletEntityMapper = walletEntityMapper;
    }

    @Override
    public Wallet save(Wallet wallet) {
        return walletEntityMapper.walletEntityToWallet(
                walletRepositoryJpa.save(walletEntityMapper.walletToWalletEntity(wallet)));
    }

    @Override
    public Wallet getByCustomerId(CustomerId customerId) {
        return walletRepositoryJpa.findByCustomerId(customerId.id())
                .map(walletEntityMapper::walletEntityToWallet)
                .orElseThrow(() -> new WalletNotFoundException("Could not find wallet with customerId: " + customerId.id()));
    }
}

@Repository
interface WalletRepositoryJpa extends CrudRepository<WalletEntity, UUID> {

    Optional<WalletEntity> findByCustomerId(UUID customerId);
}
