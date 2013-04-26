package com.stratosim.server.persistence.objectify;

import java.io.IOException;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.stratosim.server.persistence.objectify.OfyService;

@RunWith(JUnit4.class)
public class EntityEnumTest {
  
  private static final String ENTITY_PACKAGE = "com.stratosim.server.persistence.objectify.entity";
  
  private static ImmutableSet<ClassInfo> ENTITY_INFO;
  
  @BeforeClass
  public static void readClasspath() throws IOException {
    ClassPath classPath = ClassPath.from(EntityEnumTest.class.getClassLoader());
    ENTITY_INFO = FluentIterable.from(classPath.getTopLevelClassesRecursive(ENTITY_PACKAGE))
        .filter(new Predicate<ClassInfo>() {
          @Override
          public boolean apply(ClassInfo input) {
            Class<?> clazz = input.load();
            return !Modifier.isAbstract(clazz.getModifiers());
          }
        }).toSet();
  }
  
  @Test
  public void testNoSubPackages() throws IOException {
    for (ClassInfo info : ENTITY_INFO) {
      Assert.assertEquals(ENTITY_PACKAGE, info.getPackageName());
    }
  }
  
  @Test
  public void testOneToOne() {
    ImmutableSet<String> classPathEntityNames = FluentIterable.from(ENTITY_INFO)
        .transform(new Function<ClassInfo, String>() {
          @Override
          public String apply(ClassInfo input) {
            return input.getName();
          }
        }).toSet();
    ImmutableSet<String> registeredEntityNames = FluentIterable.from(OfyService.ENTITY_CLASSES)
        .transform(new Function<Class<?>, String>() {
          @Override
          public String apply(Class<?> input) {
            return input.getName();
          }
        }).toSet();
    SetView<String> diff = Sets.difference(classPathEntityNames, registeredEntityNames);
    Assert.assertEquals("diff: " + diff, classPathEntityNames, registeredEntityNames);
  }
}