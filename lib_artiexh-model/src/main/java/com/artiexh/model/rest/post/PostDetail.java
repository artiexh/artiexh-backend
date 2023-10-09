package com.artiexh.model.rest.post;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProductAttachEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.rest.account.AccountProfile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostDetail {

	private Long id;

	private String description;

	private AccountProfile owner;

	private Set<ProductAttach> attaches;

	private int likes;

	private int numOfComments;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;
}
