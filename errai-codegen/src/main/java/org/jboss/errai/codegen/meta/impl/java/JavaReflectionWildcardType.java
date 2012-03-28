/*
 * Copyright 2011 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.codegen.meta.impl.java;

import java.lang.reflect.WildcardType;

import org.jboss.errai.codegen.meta.MetaType;
import org.jboss.errai.codegen.meta.MetaWildcardType;

/**
 * @author Mike Brock <cbrock@redhat.com>
 */
public class JavaReflectionWildcardType implements MetaWildcardType {
  private WildcardType wildcardType;

  public JavaReflectionWildcardType(WildcardType wildcardType) {
    this.wildcardType = wildcardType;
  }

  @Override
  public MetaType[] getLowerBounds() {
    return JavaReflectionUtil.fromTypeArray(wildcardType.getLowerBounds());
  }

  @Override
  public MetaType[] getUpperBounds() {
    return JavaReflectionUtil.fromTypeArray(wildcardType.getUpperBounds());
  }
  
  public String toString() {
    return wildcardType.toString();
  }
}