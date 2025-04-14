package com.alexartauddev.licenseforge.web.response.realm;

import com.alexartauddev.licenseforge.web.dto.realm.RealmDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealmResponse {
    private RealmDTO realm;
}
