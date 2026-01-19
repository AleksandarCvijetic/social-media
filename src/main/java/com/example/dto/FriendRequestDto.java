package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long id;           // ID samog zahteva
    private Long senderId;     // ID korisnika koji šalje zahtev
    private String senderName; // Ime korisnika koji šalje zahtev
    private String senderEmail;// Email korisnika koji šalje zahtev
    private Long receiverId;   // ID korisnika koji prima zahtev
}
