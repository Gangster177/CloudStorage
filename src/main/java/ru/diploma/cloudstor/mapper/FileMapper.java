package ru.diploma.cloudstor.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.diploma.cloudstor.entity.CloudFile;
import ru.diploma.cloudstor.web.response.FileWebResponse;

@Component
@Mapper(componentModel = "spring")
public interface FileMapper {

    FileWebResponse cloudFileToFileWebResponse(CloudFile cloudFile);

}
