package com.example.gamify.services;

import com.example.gamify.models.dtos.input.ItemInputDTO;
import com.example.gamify.models.dtos.output.ItemOutputDTO;
import com.example.gamify.models.entities.Item;
import com.example.gamify.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public Item createNewItem(@NonNull ItemInputDTO itemInputDTO) {
        Item item = new Item(itemInputDTO.name(), itemInputDTO.value());
        return itemRepository.save(item);
    }

    public ItemOutputDTO getItemById(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + itemId));
        return item.toOutputDTO();
    }
}

