package StreamAPI;

import java.util.*;
import java.util.stream.Collectors;

public class Example1 {
    public static void main(String[] args) {
//        Integer sum = Stream.of(1, 2, 3, 4, 5, 6, 7, 7, 9, 8, 10)
//                .reduce(0, Integer::sum);
//        System.out.println(sum);

//        Stream.of(1,2,3,4,5,6,7,8,9,10)
//        .collect(Collectors.toMap(
//                arg -> arg%2,
//
//        ));

        Map<Integer, Set<Integer>> vendorShops = new HashMap<>();
        vendorShops.put(1, new HashSet<>(Arrays.asList(11,12,13,14,15)));
        vendorShops.put(2, new HashSet<>(Arrays.asList(21,22,23,24)));
        vendorShops.put(3, new HashSet<>(Arrays.asList(31,32,33,34,35,36)));

        Map<Integer,Integer> shopsBusiness = new HashMap<>();
        shopsBusiness.put(11,111);
        shopsBusiness.put(12,121);
        shopsBusiness.put(13,131);
        shopsBusiness.put(14,141);
        shopsBusiness.put(15,151);

        shopsBusiness.put(21,211);
        shopsBusiness.put(22,221);
        shopsBusiness.put(23,231);
        shopsBusiness.put(24,241);

        shopsBusiness.put(31,311);
        shopsBusiness.put(32,321);
        shopsBusiness.put(33,331);
        shopsBusiness.put(34,341);
        shopsBusiness.put(35,351);
        shopsBusiness.put(36,351);


        getBusinessMap(shopsBusiness,vendorShops).entrySet().forEach(System.out::println);

    }

    /**
     * shopsBusiness - бизнесы магазинов
     * vendorShops - магазины вендора
     *
     * return - бизнесы вендора
     */
    public static Map<Integer, Set<Integer>> getBusinessMap(
            Map<Integer, Integer> shopsBusiness,
            Map<Integer, Set<Integer>> vendorShops
            ) {

        return vendorShops.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue().stream()
                                .map(shopsBusiness::get)
                                .collect(Collectors.toSet()))
                );
    }


}
