package com.example.gamify.services;

import com.example.gamify.models.dtos.input.ItemInputDTO;
import com.example.gamify.models.dtos.output.ItemOutputDTO;
import com.example.gamify.models.entities.Item;
import com.example.gamify.models.entities.Profile;
import com.example.gamify.models.entities.Store;
import com.example.gamify.repository.ItemRepository;
import com.example.gamify.repository.ProfileRepository;
import com.example.gamify.repository.StoreRepository;
import com.example.gamify.utils.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreItemService {
    private static final Logger logger = LoggerFactory.getLogger(StoreItemService.class);

    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public ItemOutputDTO addItemToStore(UUID profileId, ItemInputDTO dto)
            throws ProfileNotFoundException, StoreNotFoundException {
        logger.info("Adicionando novo item à loja. ProfileId: {}, Nome: {}, Valor: {}", profileId, dto.name(), dto.value());

        Profile profile = profileRepository.findByUserId(profileId)
                .orElseThrow(() -> {
                    logger.warn("Perfil não encontrado. ProfileId: {}", profileId);
                    return new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + profileId);
                });

        Store store = profile.getStore();
        if (store == null) {
            logger.warn("Loja não encontrada para o perfil. ProfileId: {}", profileId);
            throw new StoreNotFoundException("Perfil não possui loja: " + profileId);
        }

        Item newItem = new Item(dto.name(), dto.value());
        store.addItem(newItem);
        storeRepository.save(store);
        logger.info("Item adicionado com sucesso. ItemId: {}, ProfileId: {}", newItem.getId(), profileId);
        return newItem.toOutputDTO();
    }

    @Transactional
    public ItemOutputDTO updateStoreItem(UUID profileId, UUID itemId, ItemInputDTO dto)
            throws ProfileNotFoundException, ItemNotFoundException, ItemNotOwnedException {
        logger.info("Atualizando item da loja. ProfileId: {}, ItemId: {}, Novo Nome: {}", profileId, itemId, dto.name());

        Item item = getOwnedItem(profileId, itemId);
        item.setName(dto.name());
        item.setValue(dto.value());
        item.setUpdated_at(LocalDateTime.now());
        itemRepository.save(item);
        logger.info("Item atualizado com sucesso. ItemId: {}, ProfileId: {}", itemId, profileId);
        return item.toOutputDTO();
    }

    @Transactional
    public void deleteStoreItem(UUID profileId, UUID itemId)
            throws ProfileNotFoundException, ItemNotFoundException, ItemNotOwnedException, ItemPurchasedException {
        logger.info("Deletando item da loja. ProfileId: {}, ItemId: {}", profileId, itemId);

        Item item = getOwnedItem(profileId, itemId);

        if (item.getPurchase_at() != null) {
            logger.warn("Tentativa de deletar item já comprado. ItemId: {}, ProfileId: {}", itemId, profileId);
            throw new ItemPurchasedException("Não é possível deletar um item que foi comprado: " + itemId);
        }

        Store store = item.getStore();
        store.removeItem(item);
        storeRepository.save(store);
        itemRepository.delete(item);
        logger.info("Item deletado com sucesso. ItemId: {}, ProfileId: {}", itemId, profileId);
    }

    @Transactional
    public ItemOutputDTO purchaseStoreItem(UUID profileId, UUID itemId)
            throws ProfileNotFoundException, ItemNotFoundException, ItemNotOwnedException, InsufficientGoldException {
        logger.info("Comprando item da loja. ProfileId: {}, ItemId: {}", profileId, itemId);

        Profile profile = profileRepository.findByUserId(profileId)
                .orElseThrow(() -> {
                    logger.warn("Perfil não encontrado na compra. ProfileId: {}", profileId);
                    return new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + profileId);
                });

        Item item = getOwnedItem(profileId, itemId);

        if (profile.getGold() < item.getValue()) {
            logger.warn("Ouro insuficiente para compra. ProfileId: {}, OuroDisponível: {}, ValorItem: {}", profileId, profile.getGold(), item.getValue());
            throw new InsufficientGoldException(
                    String.format("Ouro insuficiente: %d < %d", profile.getGold(), item.getValue())
            );
        }

        profile.setGold(profile.getGold() - item.getValue());
        item.setPurchase_at(LocalDateTime.now());

        profileRepository.save(profile);
        itemRepository.save(item);
        logger.info("Item comprado com sucesso. ItemId: {}, ProfileId: {}, OuroRestante: {}", itemId, profileId, profile.getGold());
        return item.toOutputDTO();
    }

    private Item getOwnedItem(UUID profileId, UUID itemId)
            throws ProfileNotFoundException, ItemNotFoundException, ItemNotOwnedException {
        profileRepository.findByUserId(profileId)
                .orElseThrow(() -> {
                    logger.warn("Perfil não encontrado na validação de ownership. ProfileId: {}", profileId);
                    return new ProfileNotFoundException("Não há perfil para o usuário com UUID: " + profileId);
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    logger.warn("Item não encontrado. ItemId: {}", itemId);
                    return new ItemNotFoundException("Item não encontrado com id: " + itemId);
                });

        if (item.getStore() == null || !item.getStore().getProfile().getUserId().equals(profileId)) {
            logger.warn("Item não pertence ao perfil. ItemId: {}, ProfileId: {}", itemId, profileId);
            throw new ItemNotOwnedException(
                    String.format("Item %s não pertence ao perfil %s", itemId, profileId)
            );
        }

        return item;
    }
}
