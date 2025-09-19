package az.coders.fera_project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

//
//@Component
//public class EnhancedObjectMapper {
//
//    private final ObjectMapper objectMapper;
//
//    public EnhancedObjectMapper(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    public <D, E> E convertValue(D source, Class<E> targetType) {
//        if (source == null) return null;
//        E result = objectMapper.convertValue(source, targetType);
//        GenericRelationBinder.bind(result);
//        return result;
//    }
//
//    public <E, D> List<D> convertList(List<E> source, Class<D> targetType) {
//        if (source == null) return null;
//        return objectMapper.convertValue(
//                source,
//                TypeFactory.defaultInstance().constructCollectionType(List.class, targetType)
//        );
//    }
//}


@Component
public class EnhancedObjectMapper {
    private final ObjectMapper objectMapper;

    public EnhancedObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <D, E> E convertValue(D source, Class<E> targetType) {
        E result = objectMapper.convertValue(source, targetType);
        GenericRelationBinder.bind(result);
        return result;
    }


    public <E, D> List<D> convertList(Collection<E> source, Class<D> targetType) {
        if (source == null) {
            return Collections.emptyList();
        }
        return objectMapper.convertValue(
                source,
                objectMapper.getTypeFactory().constructCollectionType(List.class, targetType)
        );
    }



    public <E, D> List<D> convertList(List<E> source, Class<D> targetType) {
        return objectMapper.convertValue(
                source,
                objectMapper.getTypeFactory().constructCollectionType(List.class, targetType)
        );
    }
}