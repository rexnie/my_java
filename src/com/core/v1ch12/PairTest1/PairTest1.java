package com.core.v1ch12.PairTest1;

/**
 * @version 1.00 2004-05-10
 * @author Cay Horstmann
 */
public class PairTest1
{
   public static void main(String[] args)
   {
      String[] words = { "Mary", "had", "a", "little", "lamb" };
      Pair<String> mm = ArrayAlg.minmax(words);
      System.out.println("min = " + mm.getFirst());
      System.out.println("max = " + mm.getSecond());

      System.out.println("middle = " + ArrayAlg.getMiddle(words));
   }
}

class ArrayAlg
{
   /**
    * Gets the minimum and maximum of an array of strings.
    * @param a an array of strings
    * @return a pair with the min and max value, or null if a is null or empty
    */
   public static Pair<String> minmax(String[] a)
   {
      if (a == null || a.length == 0) return null;
      String min = a[0];
      String max = a[0];
      for (int i = 1; i < a.length; i++)
      {
         if (min.compareTo(a[i]) > 0) min = a[i];
         if (max.compareTo(a[i]) < 0) max = a[i];
      }
      return new Pair<String>(min, max);
   }

   /**
    * 在普通类中定义范型方法，范型参数在方法修饰符和返回参数之间
    * @param a
    * @return
    */
   public static <T> T getMiddle(T[] a) {
	   return a[a.length / 2];
   }
}
