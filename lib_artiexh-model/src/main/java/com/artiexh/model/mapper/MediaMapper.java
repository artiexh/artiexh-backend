package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ImageSetEntity;
import com.artiexh.data.jpa.entity.MediaEntity;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.Media;
import com.artiexh.model.rest.customproduct.CustomProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MediaMapper {
	ImageSet entityToDomain(ImageSetEntity entity);

	Media domainToEntity(MediaEntity entity);

	@Mapping(target = "mockupImage", source = "mockupImageId", qualifiedByName = "idToDomain")
	@Mapping(target = "manufacturingImage", source = "manufacturingImageId", qualifiedByName = "idToDomain")
	ImageSet detailToDomain(CustomProductDetail.ImageSet imageSet);

	ImageSetEntity domainToEntity(ImageSet imageSet);

	CustomProductDetail.ImageSet domainToDetail(ImageSet imageSet);

	@Named("idToDomain")
	Media idToDomain(Long id);
}
