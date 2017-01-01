package com.dc.misc;

import java.util.ArrayList;
import java.util.List;

public class GenericProgramming {
	public static void main(String[] args) {
		// demo Unbounded Wildcards
		System.out.println("***********demo Unbounded Wildcards**********");
		List<String> strList = new ArrayList<String>();
		strList.add("string");
		strList.add("list");

		List<Integer> intList = new ArrayList<Integer>();
		intList.add(10);
		intList.add(20);
		intList.add(30);

		GenericProgramming.printList(strList);
		GenericProgramming.printList(intList);

		// demo Upper bounded Wildcards
		System.out.println("***********demo Upper bounded Wildcards**********");
		List<GpB> ubList = new ArrayList<GpB>();
		ubList.add(new GpB());
		ubList.add(new GpB());

		List<GpC> ucList = new ArrayList<GpC>();
		ucList.add(new GpC());
		ucList.add(new GpC());

		GenericProgramming.printList2(ubList);
		GenericProgramming.printList2(ucList);

		// demo lower bounded Wildcards
		System.out.println("***********demo lower bounded Wildcards**********");
		List<GpB> lbList = new ArrayList<GpB>();
		lbList.add(new GpB());
		lbList.add(new GpB());
		lbList.add(new GpC());
		//lbList.add(new GpA()); //编译出错

		List<GpA> laList = new ArrayList<GpA>();
		laList.add(new GpA());
		laList.add(new GpA());
		laList.add(new GpB());
		laList.add(new GpC());

		GenericProgramming.printList3(lbList);
		GenericProgramming.printList3(laList);
	}

	/**
	 * 无限定通配符(Unbounded Wildcards): <?>
	 * list 存储的可能是任意的类型，如List<String>, List<Number>, List<Integer>...
	 * <?>具有只读性：
	 * ? 表示的类型是未知的，所以不能写此List，只能读
	 */
	public static void printList(List<?> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
			System.out.println(list.get(i).getClass());
		}

		//list.add("nie"); //编译出错，未知的类型，不能写入
	}

	/**
	 * 上限通配符(upper bounded wildcards): <? extends Type>
	 * list 存储的是Type或者Type的子类型
	 * <? extends Type> 也具体只读性:
	 * ? 表示的是Type或者Type的子类型，但是具体是什么类型是未知，
	 * 所以不能写入，只能读取
	 * @param list
	 */
	public static void printList2(List<? extends GpB> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
			System.out.println(list.get(i).getClass());
		}

		//list.add(new GpB()); //编译出错，未知的类型，不能写入
	}

	/**
	 * 下限通配符(lower bounded wildcards): <? super Type>
	 * list 存储的是Type或者Type的父类型
	 * <?> 表示的是Type或者
	 * @param list
	 */
	public static void printList3(List<? super GpB> list) {
		list.add(new GpB());
		list.add(new GpC());
		//list.add(new GpA());  //编译出错
		//list.add(new Object());  //编译出错
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
			System.out.println(list.get(i).getClass());
		}
	}
}

class GpA {
}

class GpB extends GpA {
}

class GpC extends GpB {
}