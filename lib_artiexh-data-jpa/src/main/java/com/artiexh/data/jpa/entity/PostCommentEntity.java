package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post_comment")
public class PostCommentEntity {
	@Id
	@Tsid
	private Long id;

	@Column(name = "content", length = 50)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private PostEntity post;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "owner_id", nullable = false)
	private UserEntity owner;
}
