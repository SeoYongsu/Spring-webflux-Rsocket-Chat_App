package com.example.chatservicersocket.service;

import com.example.chatservicersocket.model.ChatRoom;
import com.example.chatservicersocket.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Mono<String> getChatId(String senderId, String receiverId, boolean createIfNotExist){
        return chatRoomRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .map(ChatRoom::getChatId)
        .switchIfEmpty(Mono.defer(()->{
            if(!createIfNotExist) {
                return Mono.empty();
            }
            String chatId = String.format("%s_%s", senderId, receiverId);
            ChatRoom senderRecipient = ChatRoom
                    .builder()
                    .chatId(chatId)
                    .senderId(senderId)
                    .receiverId(receiverId)
                    .build();

            ChatRoom recipientSender = ChatRoom
                    .builder()
                    .chatId(chatId)
                    .senderId(receiverId)
                    .receiverId(senderId)
                    .build();
            return chatRoomRepository.save(senderRecipient).then(chatRoomRepository.save(recipientSender))
                    .then(Mono.just(chatId));
        }));
    }



}
