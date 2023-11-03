package com.test;

import org.apache.commons.codec.binary.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public class Test1 {
	private static final char[] HEX_CHARS = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	public static void change(int i,String s,char[] c) {
		System.out.println(s);
		s = "sdfas";
		c[0] = 'X';
		i = 5;
	}

	private static void longToByteArray(long l, byte[] ba, int offset) {
		for (int i = 0; i < 8; ++i) {
			final int shift = i << 3; // i * 8
			ba[offset + 8 - 1 - i] = (byte) ((l & (0xffL << shift)) >>> shift);
		}
	}

	public static String byteToHexString(final byte[] bytes) {
		return byteToHexString(bytes, 0, bytes.length);
	}

	public static String byteToHexString(final byte[] bytes, final int start, final int end) {
		if (bytes == null) {
			throw new IllegalArgumentException("bytes == null");
		}

		int length = end - start;
		char[] out = new char[length * 2];

		for (int i = start, j = 0; i < end; i++) {
			out[j++] = HEX_CHARS[(0xF0 & bytes[i]) >>> 4];
			out[j++] = HEX_CHARS[0x0F & bytes[i]];
		}

		return new String(out);
	}

	public static void main(String[] args) {
//		System.setProperty("dddd","vvvvv");
		System.out.println(System.getProperty("dddd"));

		System.getProperties().forEach(new BiConsumer<Object, Object>() {
			@Override
			public void accept(Object o, Object o2) {
				System.out.println(o.toString()+":"+o2.toString());
			}
		});

		System.out.println("######");
		System.out.println(System.getenv("dddd"));

		Random random = new Random();
		System.out.println(random.nextInt(1));
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(random.nextInt(6));
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!");
		System.out.println(random.nextLong());
		System.out.println("~~~~~~~~~~~~~~~~~~~");

		final byte[] ba = new byte[16];
		longToByteArray(random.nextLong(), ba, 0);
		longToByteArray(random.nextLong(), ba, 8);

		System.out.println(byteToHexString(ba));
		/*long l = 10000L;
		l++;
		System.out.println(l);
		l+=12;
		System.out.println(l);
		System.out.println(Long.MAX_VALUE);
		System.out.println(Long.MIN_VALUE);
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		Integer ig = new Integer(9);*/
		/*String s = "11111111";
		char[] c = {'H','o'};
		int[] i1 = {1,2,3};
		int ii = 10;
		change(ii,s,c);
		System.out.println(ii + s + String.valueOf(c));*/
		/*int[] in = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		
		getN(in,0,0);
		
		for (int i=0,len=in.length; i<len;i++) {
			if (in[i] == 0) {
				System.out.println(i);
			}
		}*/
		
		/*List<String> list = Arrays.asList("a","f","e","d","c");
		List<String> list2 = Arrays.asList("a","f","d","c");
		Collections.sort(list);
		for (String s : list) {
			System.out.println(s);
		}
System.out.println("~~~~~~~~~~~");
		List<String> list1 = new ArrayList<String>(list);
		Iterator<String> iter = list1.iterator();
		while(iter.hasNext()){
		        String s = iter.next();
		        if(s.equals("c")){
		            iter.remove();
		    }
		}
		for (String s : list1) {
			System.out.println(s);
		}
System.out.println("~~~~~~~~~~~");
		
		boolean removeAll = list1.removeAll(list2);
		for (String s : list1) {
			System.out.println(s);
		}
		
		testList();*/
		/*FileOutputStream fos = null;
        String string = "saf dasf adf adf afd";
        boolean flag = true;
        int i = 0;
        while(flag){
        	i++;
        	if (i==200) {
        		flag = false;
        	}
	        try {
	        		fos = new FileOutputStream("d:/cs/out"+i+".txt",true);
	        		fos.write(string.getBytes());
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	            try {
	                fos.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
        }*/
	}
	
	public static void getN(int[] in,int num,int total) {
			for (int i=0,len=in.length; i<len;i++) {
				if (in[i] != 0) {
					num++;
				}
				if (num == 9) {
					total++;
					in[i] = 0;
					num=0;
				}
				if (total==15) {
					break;
				}
			}
			if (total<15) {
				getN(in, num, total);
			}
	}
	
	public static void testList() {
		  List list1 =new ArrayList();
		  list1.add("1111");
		  list1.add("2222");
		  list1.add("3333");
		  
		  List list2 =new ArrayList();
		  list2.add("3333");
		  list2.add("4444");
		  list2.add("5555");
		  
		  //并集
//		  list1.addAll(list2);
		  //交集
//		  list1.retainAll(list2);
		  //差集
		  list1.removeAll(list2);
		  //无重复并集
//		    list2.removeAll(list1);
//		    list1.addAll(list2);
		  
		 /* Iterator<String> it=list1.iterator();
		  while (it.hasNext()) {
		   System.out.println(it.next());
		   
		  }*/
		  
		  System.out.println("-----------------------------------\n");
		  printStr(list1);
	}
	public static void printStr(List list1){
		for (int i = 0; i < list1.size(); i++) {
			System.out.println(list1.get(i));
		}
	}
}
