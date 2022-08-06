package com.bigos.payment.adapters.wallet.out.repository;

import com.bigos.common.domain.vo.CustomerId;
import com.bigos.payment.adapters.wallet.exception.WalletNotFoundException;
import com.bigos.payment.adapters.wallet.model.entity.WalletEntity;
import com.bigos.payment.adapters.wallet.model.mapper.WalletEntityMapper;
import com.bigos.payment.domain.model.Wallet;
import com.bigos.payment.domain.ports.out.repository.WalletRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletRepositoryJpa walletRepositoryJpa;
    private final WalletEntityMapper walletEntityMapper;

    public WalletRepositoryImpl(WalletRepositoryJpa walletRepositoryJpa, WalletEntityMapper walletEntityMapper) {
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
