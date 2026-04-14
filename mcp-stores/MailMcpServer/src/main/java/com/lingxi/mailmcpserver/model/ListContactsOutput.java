package com.lingxi.mailmcpserver.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListContactsOutput {

    private String resultCode;

    private String resultMsg;

    private List<Map<String, String>> contacts;
}
