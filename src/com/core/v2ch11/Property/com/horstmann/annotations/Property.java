package com.core.v2ch11.Property.com.horstmann.corejava;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Property
{
   String editor() default ""; 
}
