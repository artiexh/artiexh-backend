package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.PostCommentEntity;
import com.artiexh.data.jpa.entity.ProductAttachEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Post {

	private Long id;

	private String description;

	private ArtistEntity owner;

	private Set<ProductAttachEntity> attaches;

	private int likes;

	private int numOfComments;

	private LocalDateTime createdDate;

	private LocalDateTime modifiedDate;
}
