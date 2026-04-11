package com.lingxi.mailmcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddContactOutput {

    private String resultCode;

    private String resultMsg;
}
