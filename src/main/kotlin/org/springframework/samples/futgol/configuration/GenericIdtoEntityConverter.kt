package org.springframework.samples.futgol.configuration

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.ConditionalGenericConverter
import org.springframework.core.convert.converter.GenericConverter.ConvertiblePair
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.samples.futgol.model.BaseEntity


import org.springframework.stereotype.Component
import javax.persistence.EntityManager


@Component
class GenericIdToEntityConverter : ConditionalGenericConverter {
    private val conversionService: ConversionService = DefaultConversionService()

    @Autowired(required = false)
    private val entityManager: EntityManager? = null
    override fun getConvertibleTypes(): Set<ConvertiblePair>? {
        val result: MutableSet<ConvertiblePair> = HashSet()
        result.add(ConvertiblePair(Number::class.java, BaseEntity::class.java))
        result.add(ConvertiblePair(CharSequence::class.java, BaseEntity::class.java))
        return result
    }

    override fun matches(sourceType: TypeDescriptor, targetType: TypeDescriptor): Boolean {
        return (BaseEntity::class.java.isAssignableFrom(targetType.type)
                && conversionService.canConvert(sourceType, TypeDescriptor.valueOf(Int::class.java)))
    }

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        if (source == null || entityManager == null) {
            return null
        }
        val id = conversionService.convert(
            source, sourceType, TypeDescriptor.valueOf(
                Int::class.java
            )
        ) as Int?
        val entity = entityManager.find(targetType.type, id)
        if (entity == null) {
            log.info("Did not find an entity with id {} of type {}", id, targetType.type)
            return null
        }
        return entity
    }

    companion object {
        private val log = LoggerFactory.getLogger(GenericIdToEntityConverter::class.java)
    }
}
