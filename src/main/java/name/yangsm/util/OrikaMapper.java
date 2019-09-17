package name.yangsm.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


class LocalConverter extends BidirectionalConverter<String, Integer> {
    @Override
    public Integer convertTo(String s, Type<Integer> type, MappingContext mappingContext) {
        int result = Integer.valueOf(s);
        return result;
    }

    @Override
    public String convertFrom(Integer integer, Type<String> type, MappingContext mappingContext) {
        return null;
    }
}

class LocalBigDecimalConverter extends BidirectionalConverter<String, BigDecimal> {
    @Override
    public BigDecimal convertTo(String s, Type<BigDecimal> type, MappingContext mappingContext) {
        BigDecimal bd = new BigDecimal(s);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    @Override
    public String convertFrom(BigDecimal bigDecimal, Type<String> type, MappingContext mappingContext) {
        return null;
    }
}

public class OrikaMapper {

    private static MapperFacade mapper;
    private static MapperFactory mapperFactory;

    static {
        mapperFactory = new DefaultMapperFactory.Builder().mapNulls(false).build();
        mapperFactory.getConverterFactory().registerConverter(new LocalConverter());
        mapperFactory.getConverterFactory().registerConverter(new LocalBigDecimalConverter());
        mapper = mapperFactory.getMapperFacade();

    }

    public static <S, D> void registerMap(Class<S> source, Class<D> dest, Map<String, String> mappings) {
        ClassMapBuilder<?, ?> classBuilder = mapperFactory.classMap(source, dest);
        mappings.forEach((k, v) -> classBuilder.field(k, v));
        classBuilder.byDefault().register();

    }

    public static <S, D> void map(S source, D target) {
        mapper.map(source, target);
    }

    public static <S, D> D map(S source, Class<D> detinationClass, Map<String, String> mappings) {
        return mapper.map(source, detinationClass);
    }

    /**
     * 简单的复制出新类型对象.
     * <p>
     * 通过source.getClass() 获得源Class
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return mapper.map(source, destinationClass);
    }

    /**
     *
     * 极致性能的复制出新类型对象.
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.map(source, sourceType, destinationType);
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     * <p>
     * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, sourceClass需要反射，实在有点慢
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return mapper.mapAsList(sourceList, TypeFactory.valueOf(sourceClass), TypeFactory.valueOf(destinationClass));
    }

    /**
     * 极致性能的复制出新类型对象到ArrayList.
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsList(sourceList, sourceType, destinationType);
    }

    /**
     * 简单复制出新对象列表到数组
     * <p>
     * 通过source.getComponentType() 获得源Class
     */
    public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
        return mapper.mapAsArray(destination, source, destinationClass);
    }

    /**
     * 极致性能的复制出新类型对象到数组
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return mapper.mapAsArray(destination, source, sourceType, destinationType);
    }

    /**
     * 预先获取orika转换所需要的Type，避免每次转换.
     */
    public static <E> Type<E> getType(final Class<E> rawType) {
        return TypeFactory.valueOf(rawType);
    }

}