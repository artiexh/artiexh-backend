package com.artiexh.model.rest.cart;

import com.artiexh.model.domain.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistInfoResponse {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String email;
    private Province province;
}
