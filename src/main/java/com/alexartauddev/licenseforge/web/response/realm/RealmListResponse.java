package com.alexartauddev.licenseforge.web.response.realm;

import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealmListResponse {
    private List<RealmDTO> realms;
    private long total;
    private int page;
    private int size;
}