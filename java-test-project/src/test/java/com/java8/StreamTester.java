package com.java8;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamTester {
	private enum Status {
        OPEN, CLOSED
    };
	
    private static final class Task {
        private final Status status;
        private final Integer points;
 
        Task( final Status status, final Integer points ) {
            this.status = status;
            this.points = points;
        }
         
        public Integer getPoints() {
            return points;
        }
         
        public Status getStatus() {
            return status;
        }
         
        @Override
        public String toString() {
            return String.format( "[%s, %d]", status, points );
        }
    }
    
	public static void main(String[] args) {
//		test1();
//		testInit();
//		testMap();
		testFilter();
	}
	
	private static void test1() {
		final Collection< Task > tasks = Arrays.asList(
			    new Task( Status.OPEN, 5 ),
			    new Task( Status.OPEN, 13 ),
			    new Task( Status.CLOSED, 8 ) 
			);
		// Calculate total points of all active tasks using sum()
		final long totalPointsOfOpenTasks = tasks
		    .stream()
		    .filter( task -> task.getStatus() == Status.OPEN )
		    .mapToInt( Task::getPoints )
		    .sum();
		         
		System.out.println( "Total points: " + totalPointsOfOpenTasks );
		
		// Calculate total points of all tasks
		final double totalPoints = tasks
		   .stream()
		   .parallel()
		   .map( task -> task.getPoints() ) // or map( Task::getPoints ) 
		   .reduce( 0, Integer::sum );
		     
		System.out.println( "Total points (all tasks): " + totalPoints );
		
		
		// Group tasks by their status
		final Map< Status, List< Task > > map = tasks
		    .stream()
		    .collect( Collectors.groupingBy( Task::getStatus ) );
		System.out.println( map );
		
		// Calculate the weight of each tasks (as percent of total points) 
		final Collection< String > result = tasks
		    .stream()                                        // Stream< Task >
		    .mapToInt( Task::getPoints )                     // IntStream
		    .asLongStream()                                  // LongStream
		    .mapToDouble( points -> points / totalPoints )   // DoubleStream
		    .boxed()                                         // Stream< Double >
		    .mapToLong( weigth -> ( long )( weigth * 100 ) ) // LongStream
		    .mapToObj( percentage -> percentage + "%" )      // Stream< String> 
		    .collect( Collectors.toList() );                 // List< String > 
		         
		System.out.println( result );
		
		final Path path = new File( "D:\\log\\File\\test.txt" ).toPath();
//		try( Stream< String > lines = Files.lines( path, StandardCharsets.UTF_8 ) ) {
		try( Stream< String > lines = Files.lines( path, Charset.forName("gbk") ) ) {
		    lines.onClose( () -> System.out.println("Done!") ).forEach( System.out::println );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void testInit() {
		//初始化一个流：
	    Stream<String> stream = Stream.of("a", "b", "c");
	    printStream(stream);
	    
	    //数组转换为一个流：
	    String [] strArray = new String[] {"a", "b", "c"};
//	    stream = Stream.of(strArray);
	    stream = Arrays.stream(strArray);
	    printStream(stream);
	    
	    //集合对象转换为一个流（Collections）：
	    List<String> list = Arrays.asList(strArray);
	    stream = list.stream();
	    printStream(stream);
	    
	}
	
	private static void testMap() {
		//1.1、遍历转换为大写:
		List<String> wordList = Arrays.asList("Hello","World");
		List<String> output = wordList.stream().
				map(String::toUpperCase).
				collect(Collectors.toList());
		printCollection(output,"output");
		
		//1.2、平方数：
		List<Integer> nums = Arrays.asList(1, 2, 3, 4);
		List<Integer> squareNums = nums.stream().
				map(n -> n * n).
				collect(Collectors.toList());
		printCollection(squareNums,"squareNums");
	}
	
	private static void testFilter() {
		//2.1、得到其中不为空的String
		List<String> filterLists = new ArrayList<>();
		filterLists.add("");
		filterLists.add("a");
		filterLists.add("b");
		List afterFilterLists = filterLists.stream()
		       .filter(s -> !s.isEmpty())
		        .collect(Collectors.toList());
		printCollection(afterFilterLists,"afterFilterLists");
		
		//limit 返回 Stream 的前面 n 个元素；skip 则是扔掉前 n 个元素:
		//注意skip与limit是有顺序关系的，
		//比如使用skip(2)会跳过集合的前两个，返回的为c、d、e、f,然后调用limit(3)会返回前3个，所以最后返回的c,d,e
		List<String> forEachLists = new ArrayList<>();
		forEachLists.add("a");
		forEachLists.add("b");
		forEachLists.add("c");
		forEachLists.add("d");
		forEachLists.add("e");
		forEachLists.add("f");
		List<String> limitLists = forEachLists.stream().skip(2).limit(3).collect(Collectors.toList());
		List<String> limitLists1 = forEachLists.stream().limit(3).skip(2).collect(Collectors.toList());
		printCollection(limitLists,"limitLists");
		printCollection(limitLists1,"limitLists1");
		
		//sort可以对集合中的所有元素进行排序。max，min可以寻找出流中最大或者最小的元素，而distinct可以寻找出不重复的元素：
		//5.1、对一个集合进行排序：
		List<Integer> sortLists = new ArrayList<>();
		sortLists.add(1);
		sortLists.add(4);
		sortLists.add(6);
		sortLists.add(3);
		sortLists.add(2);
		List<Integer> afterSortLists = sortLists.stream().sorted((In1,In2)->
		       In1-In2).collect(Collectors.toList());
		printCollection(afterSortLists,"afterSortLists");
		//5.2、得到其中长度最大的值：
		List<String> maxLists = new ArrayList<>();
		maxLists.add("a");
		maxLists.add("b");
		maxLists.add("c");
		maxLists.add("d");
		maxLists.add("e");
		maxLists.add("f");
		maxLists.add("hahaha");
		int maxLength = maxLists.stream().mapToInt(s->s.length()).max().getAsInt();
		System.out.println("字符串长度最长的长度为"+maxLength);
		//5.3、对一个集合进行查重：其中的distinct()方法能找出stream中元素equal()，即相同的元素，并将相同的去除，上述返回即为a,c,d。
		List<String> distinctList = new ArrayList<>();
		distinctList.add("a");
		distinctList.add("a");
		distinctList.add("c");
		distinctList.add("d");
		List<String> afterDistinctList = distinctList.stream().distinct().collect(Collectors.toList());
		printCollection(afterDistinctList,"afterDistinctList");
		
		/*
		 * 有的时候，我们只需要判断集合中是否全部满足条件，或者判断集合中是否有满足条件的元素，这时候就可以使用match方法：
		 * allMatch：Stream 中全部元素符合传入的 predicate，返回 true
		 * anyMatch：Stream 中只要有一个元素符合传入的 predicate，返回 true
		 * noneMatch：Stream 中没有一个元素符合传入的 predicate，返回 true
		 */
		//6.1、判断集合中有没有为‘c’的元素：
		List<String> matchList = new ArrayList<>();
		matchList.add("a");
		matchList.add("a");
		matchList.add("c");
		matchList.add("d"); 
		boolean isExits = matchList.stream().anyMatch(s -> s.equals("cd"));
		System.out.println(isExits);
		
		//6.2、判断集合中是否全不为空：
		List<String> matchList1 = new ArrayList<>();
		matchList1.add("a");
		matchList1.add("");
		matchList1.add("a");
		matchList1.add("c");
		matchList1.add("d");
		boolean isNotEmpty = matchList1.stream().noneMatch(s -> s.isEmpty());
		System.out.println(isNotEmpty);
	}
	
	@Test
	public void testReduce() {
		/*reduce操作可以实现从一组元素中生成一个值，sum()、max()、min()、count()等都是reduce操作，将他们单独设为函数只是因为常用。
		 * reduce()的方法定义有三种重写形式：
		 		//变形1，未定义初始值，从而第一次执行的时候第一个参数的值是Stream的第一个元素，第二个参数是Stream的第二个元素
				Optional<T> reduce(BinaryOperator<T> accumulator) 
				
				//变形2，定义了初始值，从而第一次执行的时候第一个参数的值是初始值，第二个参数是Stream的第一个元素
				T reduce(T identity, BinaryOperator<T> accumulator)
				
				//
				<U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)
		* 虽然函数定义越来越长，但语义不曾改变，多的参数只是为了指明初始值（参数identity），
		* 或者是指定并行执行时多个部分结果的合并方式（参数combiner）。reduce()最常用的场景就是从一堆值中生成一个值。
		* 用这么复杂的函数去求一个最大或最小值，你是不是觉得设计者有病。其实不然，因为“大”和“小”或者“求和”有时会有不同的语义
		*/
		
		Stream<String> stream = Stream.of("I", "lovedddd", "you", "too");
		Optional<String> longest = stream.reduce((s1, s2) -> s1.length()>=s2.length() ? s1 : s2);
		//Optional<String> longest = stream.max((s1, s2) -> s1.length()-s2.length());
		System.out.println(longest.get());
		
		Stream<String> stm = Stream.of("I", "lovedddd", "you", "too");
		Optional<String> max = stm.max((s1, s2) -> s1.length()-s2.length());
		System.out.println(max.orElseGet(()->""));
		
		// 求单词长度之和
		Stream<String> stream1 = Stream.of("I", "love", "you", "too");
		Integer lengthSum = stream1.reduce(0, // 初始值　// (1)
		        (sum, str) -> sum+str.length(), // 累加器 // (2)
		        (a, b) -> a+b); // 部分和拼接器，并行执行时才会用到 // (3)
		// int lengthSum = stream.mapToInt(str -> str.length()).sum();
		System.out.println(lengthSum);
		
		
		//求单词长度之和
		Stream<String> stream2 = Stream.of("I", "love", "you", "too");
//		Integer s_m = stream2.parallel()
		Integer s_m = stream2
			.reduce(new Integer(0), new BiFunction<Integer, String, Integer>() {
	
				@Override
				public Integer apply(Integer t, String u) {
					System.out.println("BiFunction");
					return t+u.length();
				}
				
			}, new BinaryOperator<Integer>(){
	
				@Override
				public Integer apply(Integer t, Integer u) {
					System.out.println("BinaryOperator");
					return t+u;
				}
			});
		
		System.out.println("s_m:"+s_m);
	}
	
	@Test
	public void testCollect() {
		// 将Stream转换成容器或Map
		Stream<String> stream = Stream.of("I", "love", "you", "too");
		
//		List<String> list = stream.collect(Collectors.toList()); // (1)
//		Set<String> set = stream.collect(Collectors.toSet()); // (2)
		Map<String, Integer> map = stream.collect(Collectors.toMap(Function.identity(), String::length)); // (3)
		
//		printCollection(list, "Collect");
//		printCollection(set, "Collect");
		printMap(map, "Collect");
	}
	
	private static void printStream(Stream<?> stream) {
		System.out.println("~~~~~~~~~~~~~~~");
		stream.forEach(System.out::println);
//		stream.forEach(s -> System.out.println(s));
	}
	
	private static void printCollection(Collection co,String operater) {
		System.out.println("***********");
		System.out.println(operater+":"+co);
	}
	
	private static void printMap(Map co,String operater) {
		System.out.println("***********");
		System.out.println(operater+":"+co);
	}
}
